package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.TeamMemberSnapshotDao;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;
import org.sagebionetworks.warehouse.workers.utils.TeamMemberSnapshotUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class TeamMemberSnapshotWorker extends AbstractSnapshotWorker<ObjectRecord, TeamMemberSnapshot> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedTeamMemberSnapshot";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public TeamMemberSnapshotWorker(AmazonS3Client s3Client, TeamMemberSnapshotDao dao,
			StreamResourceProvider streamResourceProvider) {
		super(s3Client, dao, streamResourceProvider);
		log = LogManager.getLogger(TeamMemberSnapshotWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<TeamMemberSnapshot> convert(ObjectRecord record) {
		TeamMemberSnapshot snapshot = TeamMemberSnapshotUtils.getTeamMemberSnapshot(record);
		if (!TeamMemberSnapshotUtils.isValidTeamMemberSnapshot(snapshot)) {
			log.error("Invalid Team Snapshot from Record: "+ (record == null ? "null" : record.toString()));
			return null;
		}
		return Arrays.asList(snapshot);
	}
}
