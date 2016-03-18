package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.audit.NodeRecord;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;

public class NodeSnapshotUtilsTest {

	/*
	 * isValidNodeSnapshot() tests
	 */
	@Test
	public void nullNodeSnapshotTest() {
		assertFalse(NodeSnapshotUtils.isValidNodeSnapshot(null));
	}

	@Test
	public void validNodeSnapshotTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		assertTrue(NodeSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullTimestampTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(NodeSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullNodeIdTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setId(null);
		assertFalse(NodeSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullNodeTypeTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setNodeType(null);
		assertFalse(NodeSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullCreatedOnTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setCreatedOn(null);
		assertFalse(NodeSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullCreatedByTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setCreatedByPrincipalId(null);
		assertFalse(NodeSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullModifiedOnTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setModifiedOn(null);
		assertFalse(NodeSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullModifiedByTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setModifiedByPrincipalId(null);
		assertFalse(NodeSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullIsPublicTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setIsPublic(null);
		assertFalse(NodeSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullIsControlledTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setIsControlled(null);
		assertFalse(NodeSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullIsRestrictedTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setIsRestricted(null);
		assertFalse(NodeSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	/*
	 * getNodeSnapshot() tests
	 */
	@Test
	public void nullTimstampGetNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		NodeRecord node = new NodeRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(NodeRecord.class.getSimpleName().toLowerCase());
		assertNull(NodeSnapshotUtils.getNodeSnapshot(record));
	}

	@Test
	public void nullJsonStringGetNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(NodeRecord.class.getSimpleName().toLowerCase());
		assertNull(NodeSnapshotUtils.getNodeSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		NodeRecord node = new NodeRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		assertNull(NodeSnapshotUtils.getNodeSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		NodeRecord node = new NodeRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(Team.class.getSimpleName().toLowerCase());
		assertNull(NodeSnapshotUtils.getNodeSnapshot(record));
	}

	@Test
	public void wrongTypeGetNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Team team = new Team();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		record.setJsonClassName(NodeRecord.class.getSimpleName().toLowerCase());
		NodeSnapshot snapshot = NodeSnapshotUtils.getNodeSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(new NodeSnapshot(), snapshot);
	}

	@Test
	public void getNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		NodeRecord node = new NodeRecord();
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
		node.setIsPublic(false);
		node.setIsControlled(true);
		node.setIsRestricted(null);
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(NodeRecord.class.getSimpleName().toLowerCase());
		NodeSnapshot snapshot = NodeSnapshotUtils.getNodeSnapshot(record);
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
		assertEquals(node.getIsPublic(), snapshot.getIsPublic());
		assertEquals(node.getIsControlled(), snapshot.getIsControlled());
		assertNull(snapshot.getIsRestricted());
	}

}
