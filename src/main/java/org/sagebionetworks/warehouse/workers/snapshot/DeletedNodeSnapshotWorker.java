package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.DeletedNodeSnapshotDao;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.warehouse.workers.model.DeletedNodeSnapshot;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.DeletedNodeSnapshotUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class DeletedNodeSnapshotWorker extends AbstractSnapshotWorker<ObjectRecord, DeletedNodeSnapshot> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedDeletedNodeSnapshot";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public DeletedNodeSnapshotWorker(AmazonS3Client s3Client, DeletedNodeSnapshotDao dao,
			StreamResourceProvider streamResourceProvider, AmazonLogger amazonLogger) {
		super(s3Client, dao, streamResourceProvider, amazonLogger);
		log = LogManager.getLogger(DeletedNodeSnapshotWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<DeletedNodeSnapshot> convert(ObjectRecord record) {
		DeletedNodeSnapshot snapshot = DeletedNodeSnapshotUtils.getDeletedNodeSnapshot(record);
		if (!DeletedNodeSnapshotUtils.isValidDeletedNodeSnapshot(snapshot)) {
			throw new IllegalArgumentException("Invalid DeletedNodeSnapshot from Record: "+ (record == null ? "null" : record.toString()));
		}
		return Arrays.asList(snapshot);
	}
}
