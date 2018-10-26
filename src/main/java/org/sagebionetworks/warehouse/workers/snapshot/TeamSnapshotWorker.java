package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.TeamSnapshotDao;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;
import org.sagebionetworks.warehouse.workers.utils.PrincipalSnapshotUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class TeamSnapshotWorker extends AbstractSnapshotWorker<ObjectRecord, TeamSnapshot> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedTeamSnapshot";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public TeamSnapshotWorker(AmazonS3Client s3Client, TeamSnapshotDao dao,
			StreamResourceProvider streamResourceProvider, AmazonLogger amazonLogger) {
		super(s3Client, dao, streamResourceProvider, amazonLogger);
		log = LogManager.getLogger(TeamSnapshotWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<TeamSnapshot> convert(ObjectRecord record) {
		TeamSnapshot snapshot = PrincipalSnapshotUtils.getTeamSnapshot(record);
		if (!PrincipalSnapshotUtils.isValidTeamSnapshot(snapshot)) {
			throw new IllegalArgumentException("Invalid Team Snapshot from Record: "+ (record == null ? "null" : record.toString()));
		}
		return Arrays.asList(snapshot);
	}
}
