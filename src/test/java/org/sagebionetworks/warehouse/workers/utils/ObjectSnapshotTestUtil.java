package org.sagebionetworks.warehouse.workers.utils;

import java.util.Date;
import java.util.Random;

import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;
import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;
import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;
import org.sagebionetworks.warehouse.workers.model.UserProfileSnapshot;

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

	/**
	 * 
	 * @return a unique valid TeamMemberSnapshot
	 */
	public static TeamMemberSnapshot createValidTeamMemberSnapshot() {
		TeamMemberSnapshot snapshot = new TeamMemberSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		snapshot.setTeamId(random.nextLong());
		snapshot.setMemberId(random.nextLong());
		return snapshot;
	}

	/**
	 * 
	 * @return a unique valid UserProfileSnapshot
	 */
	public static UserProfileSnapshot createValidUserProfileSnapshot() {
		UserProfileSnapshot snapshot = new UserProfileSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		snapshot.setOwnerId("" + random.nextLong());
		snapshot.setUserName("username");
		return snapshot;
	}
}
