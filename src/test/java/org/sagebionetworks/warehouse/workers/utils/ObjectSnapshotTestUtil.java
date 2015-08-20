package org.sagebionetworks.warehouse.workers.utils;

import java.util.Date;
import java.util.Random;

import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;

public class ObjectSnapshotTestUtil {
	private static Random random = new Random();

	/**
	 * 
	 * @return a unique valid NodeSnapshot
	 */
	public static NodeSnapshot createValidNodeSnapshot() {
		NodeSnapshot snapshot = new NodeSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		snapshot.setId("" + random.nextLong());
		snapshot.setNodeType(EntityType.values()[random.nextInt(5)]);
		snapshot.setCreatedByPrincipalId(random.nextLong());
		snapshot.setCreatedOn(new Date());
		snapshot.setModifiedByPrincipalId(random.nextLong());
		snapshot.setModifiedOn(new Date());
		return snapshot;
	}
}
