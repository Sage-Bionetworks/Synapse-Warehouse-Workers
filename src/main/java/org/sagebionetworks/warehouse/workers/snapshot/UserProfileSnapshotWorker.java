package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.UserProfileSnapshotDao;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.model.UserProfileSnapshot;
import org.sagebionetworks.warehouse.workers.utils.PrincipalSnapshotUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class UserProfileSnapshotWorker extends AbstractSnapshotWorker<ObjectRecord, UserProfileSnapshot> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedUserProfileSnapshot";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public UserProfileSnapshotWorker(AmazonS3Client s3Client, UserProfileSnapshotDao dao,
			StreamResourceProvider streamResourceProvider) {
		super(s3Client, dao, streamResourceProvider);
		log = LogManager.getLogger(UserProfileSnapshotWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<UserProfileSnapshot> convert(ObjectRecord record) {
		UserProfileSnapshot snapshot = PrincipalSnapshotUtils.getUserProfileSnapshot(record);
		if (!PrincipalSnapshotUtils.isValidUserProfileSnapshot(snapshot)) {
			log.error("Invalid UserProfile Snapshot from Record: "+ (record == null ? "null" : record.toString()));
			return null;
		}
		return Arrays.asList(snapshot);
	}
}
