package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.ProcessedAccessRecordDao;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

/**
 * This worker reader a collated access record file from S3, processes it,
 * and write the processed data to PROCESSED_ACCESS_RECORD table.
 */
public class ProcessAccessRecordWorker extends AbstractSnapshotWorker<AccessRecord, ProcessedAccessRecord> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedAccessRecordsToProcess";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public ProcessAccessRecordWorker(AmazonS3Client s3Client, ProcessedAccessRecordDao dao,
			StreamResourceProvider streamResourceProvider) {
		super(s3Client, dao, streamResourceProvider);
		log = LogManager.getLogger(ProcessAccessRecordWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.ACCESS_RECORD_HEADERS;
		clazz = AccessRecord.class;
	}

	@Override
	public List<ProcessedAccessRecord> convert(AccessRecord record) {
		if (!AccessRecordUtils.isValidAccessRecord(record)) {
			log.error("Invalid Access Record: "+ (record == null ? "null" : record.toString()));
			return null;
		}
		return Arrays.asList(AccessRecordUtils.processAccessRecord(record));
	}

}
