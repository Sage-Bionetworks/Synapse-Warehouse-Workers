package org.sagebionetworks.warehouse.workers.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.repo.model.Node;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
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

	/**
	 * 
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static ObjectRecord createValidNodeObjectRecord() throws JSONObjectAdapterException {
		Node node = new Node();
		node.setId("" + random.nextLong());
		node.setNodeType(EntityType.values()[random.nextInt(5)]);
		node.setCreatedByPrincipalId(random.nextLong());
		node.setCreatedOn(new Date());
		node.setModifiedByPrincipalId(random.nextLong());
		node.setModifiedOn(new Date());
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		return record;
	}

	/**
	 * 
	 * @param size
	 * @return a batch of size valid NodeSnapshots
	 * @throws JSONObjectAdapterException 
	 */
	public static List<ObjectRecord> createValidNodeSnapshotBatch(int size) throws JSONObjectAdapterException {
		List<ObjectRecord> batch = new ArrayList<ObjectRecord>();
		for (int i = 0; i < size; i++) {
			batch.add(createValidNodeObjectRecord());
		}
		return batch;
	}

	/**
	 * 
	 * @param size
	 * @return
	 * @throws JSONObjectAdapterException 
	 */
	public static List<ObjectRecord> createValidTeamSnapshotBatch(int size) throws JSONObjectAdapterException {
		List<ObjectRecord> batch = new ArrayList<ObjectRecord>();
		for (int i = 0; i < size; i++) {
			batch.add(createValidTeamObjectRecord());
		}
		return batch;
	}

	/**
	 * 
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static ObjectRecord createValidTeamObjectRecord() throws JSONObjectAdapterException {
		Team team = new Team();
		team.setId("" + random.nextLong());
		team.setCreatedBy("" + random.nextLong());
		team.setCreatedOn(new Date());
		team.setModifiedBy("" + random.nextLong());
		team.setModifiedOn(new Date());
		team.setCanPublicJoin(true);
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(Team.class.getSimpleName().toLowerCase());
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		return record;
	}
}
