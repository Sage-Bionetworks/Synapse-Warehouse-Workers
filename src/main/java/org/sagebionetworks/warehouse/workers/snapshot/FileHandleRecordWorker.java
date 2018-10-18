package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.FileHandleSnapshot;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.FileHandleRecordDao;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.FileHandleSnapshotUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class FileHandleRecordWorker extends AbstractSnapshotWorker<ObjectRecord, FileHandleSnapshot> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedFileHandleRecord";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public FileHandleRecordWorker(AmazonS3Client s3Client, FileHandleRecordDao dao,
			StreamResourceProvider streamResourceProvider, AmazonLogger amazonLogger) {
		super(s3Client, dao, streamResourceProvider, amazonLogger);
		log = LogManager.getLogger(FileHandleRecordWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<FileHandleSnapshot> convert(ObjectRecord record) {
		FileHandleSnapshot snapshot = FileHandleSnapshotUtils.getFileHandleSnapshot(record);
		if (!FileHandleSnapshotUtils.isValidFileHandleSnapshot(snapshot)) {
			throw new IllegalArgumentException("Invalid FileHandleSnapshot from Record: "+ (record == null ? "null" : record.toString()));
		}
		return Arrays.asList(snapshot);
	}
}
