package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
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

public class ObjectSnapshotUtilsTest {

	/*
	 * isValidNodeSnapshot() tests
	 */
	@Test
	public void nullNodeSnapshotTest() {
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(null));
	}

	@Test
	public void validNodeSnapshotTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		assertTrue(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullTimestampTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullNodeIdTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setId(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullNodeTypeTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setNodeType(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullCreatedOnTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setCreatedOn(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullCreatedByTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setCreatedByPrincipalId(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullModifiedOnTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setModifiedOn(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullModifiedByTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setModifiedByPrincipalId(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	/*
	 * isValidTeamSnapshot() tests
	 */
	@Test
	public void nullTeamSnapshotTest() {
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(null));
	}

	@Test
	public void validTeamSnapshotTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		assertTrue(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullTimestampTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullIdTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setId(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullCreatedOnTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setCreatedOn(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullCreatedByTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setCreatedBy(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullModifiedOnTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setModifiedOn(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullModifiedByTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setModifiedBy(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullCanPublicJoinTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setCanPublicJoin(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	/*
	 * isValidTeamMemberSnapshot() tests
	 */
	@Test
	public void nullTeamMemberSnapshotTest() {
		assertFalse(ObjectSnapshotUtils.isValidTeamMemberSnapshot(null));
	}

	@Test
	public void validTeamMemberSnapshotTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		assertTrue(ObjectSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	@Test
	public void invalidTeamMemberSnapshotWithNullTimestampTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	@Test
	public void invalidTeamMemberSnapshotWithNullTeamIdTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot.setTeamId(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	@Test
	public void invalidTeamMemberSnapshotWithNullMemberIdTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot.setMemberId(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	/*
	 * isValidUserProfileSnapshot() tests
	 */
	@Test
	public void nullUserProfileSnapshotTest() {
		assertFalse(ObjectSnapshotUtils.isValidUserProfileSnapshot(null));
	}

	@Test
	public void validUserProfileSnapshotTest() {
		UserProfileSnapshot snapshot = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		assertTrue(ObjectSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	@Test
	public void invalidUserProfileSnapshotWithNullTimestampTest() {
		UserProfileSnapshot snapshot = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(ObjectSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	@Test
	public void invalidUserProfileSnapshotWithNullIdTest() {
		UserProfileSnapshot snapshot = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		snapshot.setOwnerId(null);
		assertFalse(ObjectSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	@Test
	public void invalidUserProfileSnapshotWithNullUserNameTest() {
		UserProfileSnapshot snapshot = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		snapshot.setUserName(null);
		assertFalse(ObjectSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	/*
	 * getNodeSnapshot() tests
	 */
	@Test
	public void wrongTypeNameGetNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Team team = new Team();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		record.setJsonClassName(Team.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getNodeSnapshot(record));
	}

	@Test
	public void wrongTypeGetNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Team team = new Team();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		NodeSnapshot snapshot = ObjectSnapshotUtils.getNodeSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(new NodeSnapshot(), snapshot);
	}

	@Test
	public void getNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		node.setId("id");
		node.setBenefactorId("benefactorId");
		node.setProjectId("projectId");
		node.setParentId("parentId");
		node.setNodeType(EntityType.file);
		node.setCreatedOn(new Date(0));
		node.setCreatedByPrincipalId(1L);
		node.setModifiedOn(new Date());
		node.setModifiedByPrincipalId(2L);
		node.setVersionNumber(3L);
		node.setFileHandleId("fileHandleId");
		node.setName("name");
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		NodeSnapshot snapshot = ObjectSnapshotUtils.getNodeSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(timestamp, snapshot.getTimestamp());
		assertEquals(node.getId(), snapshot.getId());
		assertEquals(node.getBenefactorId(), snapshot.getBenefactorId());
		assertEquals(node.getProjectId(), snapshot.getProjectId());
		assertEquals(node.getParentId(), snapshot.getParentId());
		assertEquals(node.getCreatedOn(), snapshot.getCreatedOn());
		assertEquals(node.getCreatedByPrincipalId(), snapshot.getCreatedByPrincipalId());
		assertEquals(node.getModifiedOn(), snapshot.getModifiedOn());
		assertEquals(node.getModifiedByPrincipalId(), snapshot.getModifiedByPrincipalId());
		assertEquals(node.getNodeType(), snapshot.getNodeType());
		assertEquals(node.getVersionNumber(), snapshot.getVersionNumber());
		assertEquals(node.getFileHandleId(), node.getFileHandleId());
		assertEquals(node.getName(), snapshot.getName());
	}
}
