package org.sagebionetworks.warehouse.workers.collate;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.sagebionetworks.aws.utils.s3.BucketDao;
import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;
import org.sagebionetworks.warehouse.workers.BucketDaoProvider;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfo;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfoList;
import org.sagebionetworks.warehouse.workers.bucket.FolderDto;
import org.sagebionetworks.warehouse.workers.db.FolderMetadataDao;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

public class FolderCollateWorkerTest {
	
	BucketDaoProvider mockBucketDaoProvider;
	S3ObjectCollator mockCollator;
	BucketInfoList bucketList;
	FolderMetadataDao mockFolderMetadataDao;
	BucketDao mockBucketDao;
	BucketInfo bucketInfo;
	ProgressCallback<Void> mockProgressCallback;
	FolderDto folder;
	List<String> keysInBucket;
	FolderCollateWorker worker;
	
	@Before
	public void before(){
		mockBucketDaoProvider = Mockito.mock(BucketDaoProvider.class);
		mockCollator = Mockito.mock(S3ObjectCollator.class);
		mockFolderMetadataDao = Mockito.mock(FolderMetadataDao.class);
		mockProgressCallback = Mockito.mock(ProgressCallback.class);
		mockBucketDao = Mockito.mock(BucketDao.class);
		bucketInfo = new BucketInfo();
		bucketInfo.setBucketName("someBucket");
		bucketInfo.setTimestampColumnIndex(3);
		bucketList = new BucketInfoList(Arrays.asList(bucketInfo));
		folder = new FolderDto();
		folder.setBucket(bucketInfo.getBucketName());
		folder.setPath("somePath");
		
		keysInBucket = Arrays.asList(
				"bucket/123/2015-07-28/16-40-30-522-uuid.csv.gz",
				"bucket/123/2015-07-28/16-40-30-522-uuid-rolling.csv.gz",
				"bucket/123/2015-07-28/16-42-30-522-uuid-rolling.csv.gz",
				"bucket/123/2015-07-28/16-47-30-521-uuid-rolling.csv.gz"
		);
		
		
		when(mockBucketDaoProvider.createBucketDao(anyString())).thenReturn(mockBucketDao);
		when(mockBucketDao.keyIterator(folder.getPath())).thenReturn(keysInBucket.iterator());
		
		worker = new FolderCollateWorker(mockBucketDaoProvider, mockCollator, bucketList, mockFolderMetadataDao);
	}
	
	@Test
	public void testRunWhileHoldingLock(){
		// call under test
		worker.runWhileHoldingLock(mockProgressCallback, folder);
	}
	
	@Test
	public void testCreateHoursOfDayAndMinutesIntervalZero(){
		int intervalMinutes = 5;
	    Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		assertEquals("00-00-00-000", FolderCollateWorker.createHoursOfDayAndMinutesInterval(cal.getTimeInMillis(), intervalMinutes));
	}
	
	@Test
	public void testCreateHoursOfDayAndMinutesIntervalOne(){
		int intervalMinutes = 5;
	    Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 1);
		assertEquals("01-00-00-000", FolderCollateWorker.createHoursOfDayAndMinutesInterval(cal.getTimeInMillis(), intervalMinutes));
	}
	
	@Test
	public void testCreateHoursOfDayAndMinutesIntervalMiddle(){
		int intervalMinutes = 5;
	    Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 23);
		assertEquals("23-20-00-000", FolderCollateWorker.createHoursOfDayAndMinutesInterval(cal.getTimeInMillis(), intervalMinutes));
	}
	
	@Test
	public void testCreateHoursOfDayAndMinutesIntervalAtInterval(){
		int intervalMinutes = 5;
	    Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 5);
		assertEquals("23-05-00-000", FolderCollateWorker.createHoursOfDayAndMinutesInterval(cal.getTimeInMillis(), intervalMinutes));
	}
	
	@Test
	public void testCreateHoursOfDayAndMinutesIntervalAtTopOfTheHour(){
		int intervalMinutes = 5;
	    Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		assertEquals("23-55-00-000", FolderCollateWorker.createHoursOfDayAndMinutesInterval(cal.getTimeInMillis(), intervalMinutes));
	}
	

}
