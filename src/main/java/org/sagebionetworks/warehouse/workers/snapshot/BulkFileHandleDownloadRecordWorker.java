package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.FileHandleDownloadRecordDao;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.warehouse.workers.model.FileHandleDownload;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.FileDownloadRecordUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class BulkFileHandleDownloadRecordWorker extends AbstractSnapshotWorker<ObjectRecord, FileHandleDownload> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedBulkFileHanldeDownloadRecord";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public BulkFileHandleDownloadRecordWorker(AmazonS3Client s3Client, FileHandleDownloadRecordDao dao,
			StreamResourceProvider streamResourceProvider, AmazonLogger amazonLogger) {
		super(s3Client, dao, streamResourceProvider, amazonLogger);
		log = LogManager.getLogger(BulkFileHandleDownloadRecordWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<FileHandleDownload> convert(ObjectRecord record) {
		List<FileHandleDownload> records = FileDownloadRecordUtils.getFileHandleDownloadRecordsForBulkFileDownloadRecord(record);
		if (!FileDownloadRecordUtils.isValidFileHandleDownloadRecords(records)) {
			throw new IllegalArgumentException("Invalid BulkFileDownloadRecord from Record: "+ (record == null ? "null" : record.toString()));
		}
		return records;
	}
}
