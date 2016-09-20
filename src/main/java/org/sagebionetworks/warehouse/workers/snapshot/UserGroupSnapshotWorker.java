package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.UserGroup;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.UserGroupDao;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.PrincipalSnapshotUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class UserGroupSnapshotWorker extends AbstractSnapshotWorker<ObjectRecord, UserGroup> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedUserGroupSnapshot";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public UserGroupSnapshotWorker(AmazonS3Client s3Client, UserGroupDao dao,
			StreamResourceProvider streamResourceProvider) {
		super(s3Client, dao, streamResourceProvider);
		log = LogManager.getLogger(UserGroupSnapshotWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<UserGroup> convert(ObjectRecord record) {
		UserGroup snapshot = PrincipalSnapshotUtils.getUserGroupSnapshot(record);
		if (!PrincipalSnapshotUtils.isValidUserGroupSnapshot(snapshot)) {
			log.error("Invalid UserGroup Snapshot from Record: "+ (record == null ? "null" : record.toString()));
			return null;
		}
		return Arrays.asList(snapshot);
	}

}
