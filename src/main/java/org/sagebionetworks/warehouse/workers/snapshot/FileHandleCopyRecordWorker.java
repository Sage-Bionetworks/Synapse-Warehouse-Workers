package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.FileHandleCopyRecordDao;
import org.sagebionetworks.warehouse.workers.model.FileHandleCopyRecordSnapshot;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.FileHandleCopyRecordUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class FileHandleCopyRecordWorker extends AbstractSnapshotWorker<ObjectRecord, FileHandleCopyRecordSnapshot> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedFileHanldeCopyRecord";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public FileHandleCopyRecordWorker(AmazonS3Client s3Client, FileHandleCopyRecordDao dao,
			StreamResourceProvider streamResourceProvider) {
		super(s3Client, dao, streamResourceProvider);
		log = LogManager.getLogger(FileHandleCopyRecordWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<FileHandleCopyRecordSnapshot> convert(ObjectRecord record) {
		FileHandleCopyRecordSnapshot snapshot = FileHandleCopyRecordUtils.getFileHandleCopyRecordSnapshot(record);
		if (!FileHandleCopyRecordUtils.isValidFileHandleCopyRecordSnapshot(snapshot)) {
			log.error("Invalid FileDownloadRecord from Record: "+ (record == null ? "null" : record.toString()));
			return null;
		}
		return Arrays.asList(snapshot);
	}
}
