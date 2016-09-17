package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.NodeSnapshotDao;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.NodeSnapshotUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class NodeSnapshotWorker extends AbstractSnapshotWorker<ObjectRecord, NodeSnapshot> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedNodeSnapshot";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public NodeSnapshotWorker(AmazonS3Client s3Client, NodeSnapshotDao dao,
			StreamResourceProvider streamResourceProvider) {
		super(s3Client, dao, streamResourceProvider);
		log = LogManager.getLogger(NodeSnapshotWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<NodeSnapshot> convert(ObjectRecord record) {
		NodeSnapshot snapshot = NodeSnapshotUtils.getNodeSnapshot(record);
		if (!NodeSnapshotUtils.isValidNodeSnapshot(snapshot)) {
			log.error("Invalid Node Snapshot from Record: "+ (record == null ? "null" : record.toString()));
			return null;
		}
		return Arrays.asList(snapshot);
	}
}
