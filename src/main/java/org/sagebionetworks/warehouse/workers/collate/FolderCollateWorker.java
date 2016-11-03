package org.sagebionetworks.warehouse.workers.collate;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.aws.utils.s3.BucketDao;
import org.sagebionetworks.aws.utils.s3.KeyData;
import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;
import org.sagebionetworks.warehouse.workers.BucketDaoProvider;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfo;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfoList;
import org.sagebionetworks.warehouse.workers.db.FolderMetadataDao;
import org.sagebionetworks.warehouse.workers.model.FolderState;
import org.sagebionetworks.common.util.progress.ProgressCallback;

public class FolderCollateWorker implements LockedFolderRunner {
	
	static private Logger log = LogManager.getLogger(FolderCollateWorker.class);
	
	private static final String TEMPLATE_HOURS_OF_DAY_MIN_INTERVAL = "%1$02d-%2$02d-00-000";
	/*
	 * Determines how files within the same folder will be collated.
	 * With a 5 minute interval there will be 12 collated files for each hour.
	 */
	public static int COLLATE_INTERVAL_MINUTES = 5;
	/*
	 * Determines the age a rolling file must be before it will be considered for collation.
	 */
	public static long COLLATE_ROLLING_OLDER_THAN_MS = 1000*60*COLLATE_INTERVAL_MINUTES*2;
	
	BucketDaoProvider bucketDaoProvider;
	S3ObjectCollator collator;
	Map<String, Integer> bucketToSortColumnMap;
	FolderMetadataDao folderMetadataDao;
	
	@Inject
	public FolderCollateWorker(BucketDaoProvider bucketDaoProvider, S3ObjectCollator collator, BucketInfoList bucketList, FolderMetadataDao folderMetadataDao) {
		super();
		this.bucketDaoProvider = bucketDaoProvider;
		this.collator = collator;
		this.folderMetadataDao = folderMetadataDao;
		// Map bucket name to sort index.
		bucketToSortColumnMap = new HashMap<String, Integer>();
		for(BucketInfo info: bucketList.getBucketList()){
			bucketToSortColumnMap.put(info.getBucketName(), info.getTimestampColumnIndex());
		}
	}


	@Override
	public void runWhileHoldingLock(ProgressCallback<Void> progressCallback,
			FolderState folder) {
		log.info("Working on folder: "+folder.getPath());
		// walk all files in this folder
		BucketDao bucketDao = this.bucketDaoProvider.createBucketDao(folder.getBucket());
		Iterator<String> keyIterator = bucketDao.keyIterator(folder.getPath());
		// Only rolling files oder than this will be collated with this run.
		long cutOffTimeMS = System.currentTimeMillis()-COLLATE_ROLLING_OLDER_THAN_MS;
		// groups files to be collated by hour and minute intervals.
		Map<String, List<String>> toCollateGroups = new HashMap<String, List<String>>();
		// track if all files were collated in this folder.
		boolean wereAllFilesCollated = true;
		// group each key COLLATE_INTERVAL_MINUTES minute intervals.
		while(keyIterator.hasNext()){
			progressCallback.progressMade(null);
			String key = keyIterator.next();
			KeyData keyData = KeyGeneratorUtil.parseKey(key);
			// only looking for rolling files
			if(keyData.isRolling()){
				// only look at files that are older than the interval.
				if(keyData.getTimeMS() < cutOffTimeMS){
					// Create the string that represents the group that this file belongs too.
					String hourMinutesGroup = createHoursOfDayAndMinutesInterval(keyData.getTimeMS(), COLLATE_INTERVAL_MINUTES);
					List<String> keysForGroup = toCollateGroups.get(hourMinutesGroup);
					if(keysForGroup == null){
						keysForGroup = new LinkedList<String>();
						toCollateGroups.put(hourMinutesGroup, keysForGroup);
					}
					keysForGroup.add(key);
				}else{
					// this file will not be collated this round
					wereAllFilesCollated = false;
				}
			}
		}
		// get the sort column for this bucket.
		int sortColumnIndex = bucketToSortColumnMap.get(folder.getBucket());

		// Collate each group
		for(String hourMinutesGroup: toCollateGroups.keySet()){
			progressCallback.progressMade(null);
			List<String> keysToCollate = toCollateGroups.get(hourMinutesGroup);
			String destinationKey = createDestinationKey(folder.getPath(), hourMinutesGroup);
			try {
				collator.replaceCSVsWithCollatedCSV(progressCallback, folder.getBucket(), keysToCollate, destinationKey, sortColumnIndex);
				log.info("Collated :"+keysToCollate.size()+" files into: "+destinationKey);
			} catch (Exception e) {
				wereAllFilesCollated = false;
				// log the exception and the keys
				log.error("Failed to collate: ", e);
				for(String key: keysToCollate){
					log.error("Failed to collate: "+key);
				}
			}
		}
		// If all files were collated set the state of the folder to be collated
		if(wereAllFilesCollated){
			FolderState state = new FolderState();
			state.setBucket(folder.getBucket());
			state.setPath(folder.getPath());
			state.setState(FolderState.State.COLLATED);
			state.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
			folderMetadataDao.createOrUpdateFolderState(state);
		}
	}
	
	/**
	 * Create a string consisting of the hours of the day (0-23) and minutes group by the given period.
	 * For example: Both 13:29 and 13:20 would result in '13-20' for a period of 10 minutes.
	 * @param timesMS
	 * @param periodMins
	 * @return
	 */
	public static String createHoursOfDayAndMinutesInterval(long timesMS, int intervalMins){
		Calendar cal = KeyGeneratorUtil.getCalendarUTC(timesMS);
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int minutePeriod = (minute/intervalMins)*intervalMins;
		return String.format(TEMPLATE_HOURS_OF_DAY_MIN_INTERVAL, hourOfDay, minutePeriod);
	}
	
	/**
	 * Create a new key for a collation destination file.
	 * @param path The 
	 * @param hourMinutesGroup
	 * @return
	 */
	public static String createDestinationKey(String path, String hourMinutesGroup){
		return path+"/"+hourMinutesGroup+"-"+UUID.randomUUID().toString()+".csv.gz";
	}

}
