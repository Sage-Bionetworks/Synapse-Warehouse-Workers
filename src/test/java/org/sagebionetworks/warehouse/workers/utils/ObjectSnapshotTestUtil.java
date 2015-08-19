package org.sagebionetworks.warehouse.workers.utils;

import java.util.Date;
import java.util.Random;

import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;
import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;

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

	/**
	 * 
	 * @return a unique valid TeamSnapshot
	 */
	public static TeamSnapshot createValidTeamSnapshot() {
		TeamSnapshot snapshot = new TeamSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		snapshot.setId("" + random.nextLong());
		snapshot.setCreatedBy("" + random.nextLong());
		snapshot.setCreatedOn(new Date());
		snapshot.setModifiedBy("" + random.nextLong());
		snapshot.setModifiedOn(new Date());
		snapshot.setCanPublicJoin(true);
		return snapshot;
	}
}
