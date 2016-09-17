package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.BulkFileDownloadRecordDao;
import org.sagebionetworks.warehouse.workers.model.BulkFileDownloadRecord;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.BulkFileDownloadRecordUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class BulkFileDownloadRecordWorker extends AbstractSnapshotWorker<ObjectRecord, BulkFileDownloadRecord> implements SnapshotWorker<ObjectRecord, BulkFileDownloadRecord> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedBulkFileDownloadRecord";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public BulkFileDownloadRecordWorker(AmazonS3Client s3Client, BulkFileDownloadRecordDao dao,
			StreamResourceProvider streamResourceProvider) {
		super(s3Client, dao, streamResourceProvider);
		log = LogManager.getLogger(BulkFileDownloadRecordWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<BulkFileDownloadRecord> convert(ObjectRecord record) {
		List<BulkFileDownloadRecord> records = BulkFileDownloadRecordUtils.getBulkFileDownloadRecords(record);
		if (!BulkFileDownloadRecordUtils.isValidBulkFileDownloadRecords(records)) {
			log.error("Invalid BulkFileDownloadRecord from Record: " + record.toString());
			return null;
		}
		return records;
	}
}
