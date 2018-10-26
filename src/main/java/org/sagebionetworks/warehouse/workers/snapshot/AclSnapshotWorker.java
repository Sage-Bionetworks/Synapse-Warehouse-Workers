package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.AclSnapshotDao;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.warehouse.workers.model.AclSnapshot;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.AclSnapshotUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class AclSnapshotWorker extends AbstractSnapshotWorker<ObjectRecord, AclSnapshot>{

	public static final String TEMP_FILE_NAME_PREFIX = "collatedAclRecordSnapshot";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public AclSnapshotWorker(AmazonS3Client s3Client, AclSnapshotDao aclSnapshotDao,
			StreamResourceProvider streamResourceProvider, AmazonLogger amazonLogger) {
		super(s3Client, aclSnapshotDao, streamResourceProvider, amazonLogger);
		log = LogManager.getLogger(AclSnapshotWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<AclSnapshot> convert(ObjectRecord record) {
		AclSnapshot snapshot = AclSnapshotUtils.getAclSnapshot(record);
		if (!AclSnapshotUtils.isValidAclSnapshot(snapshot)) {
			throw new IllegalArgumentException("Invalid Acl Snapshot from Record: "+ (record == null ? "null" : record.toString()));
		}
		return Arrays.asList(snapshot);
	}
}
