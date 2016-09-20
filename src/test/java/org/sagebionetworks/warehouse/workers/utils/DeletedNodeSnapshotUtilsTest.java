package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.audit.DeletedNode;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.DeletedNodeSnapshot;

public class DeletedNodeSnapshotUtilsTest {

	/*
	 * isValidDeletedNodeSnapshot() tests
	 */
	@Test
	public void nullDeletedNodeSnapshotTest() {
		assertFalse(DeletedNodeSnapshotUtils.isValidDeletedNodeSnapshot(null));
	}

	@Test
	public void validDeletedNodeSnapshotTest() {
		DeletedNodeSnapshot snapshot = new DeletedNodeSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		snapshot.setId(123);
		assertTrue(DeletedNodeSnapshotUtils.isValidDeletedNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullIdTest() {
		DeletedNodeSnapshot snapshot = new DeletedNodeSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		assertFalse(DeletedNodeSnapshotUtils.isValidDeletedNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullTimestampTest() {
		DeletedNodeSnapshot snapshot = new DeletedNodeSnapshot();
		snapshot.setId(123);
		assertFalse(DeletedNodeSnapshotUtils.isValidDeletedNodeSnapshot(snapshot));
	}

	/*
	 * getNodeSnapshot() tests
	 */
	@Test
	public void nullTimstampGetDeletedNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		DeletedNode deletedNode = new DeletedNode();
		record.setJsonString(EntityFactory.createJSONStringForEntity(deletedNode));
		record.setJsonClassName(DeletedNode.class.getSimpleName().toLowerCase());
		assertNull(DeletedNodeSnapshotUtils.getDeletedNodeSnapshot(record));
	}

	@Test
	public void nullJsonStringGetDeletedNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(DeletedNode.class.getSimpleName().toLowerCase());
		assertNull(DeletedNodeSnapshotUtils.getDeletedNodeSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetDeletedNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		DeletedNode deletedNode = new DeletedNode();
		record.setJsonString(EntityFactory.createJSONStringForEntity(deletedNode));
		assertNull(DeletedNodeSnapshotUtils.getDeletedNodeSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetDeletedNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		DeletedNode deletedNode = new DeletedNode();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(deletedNode));
		record.setJsonClassName(Team.class.getSimpleName().toLowerCase());
		assertNull(DeletedNodeSnapshotUtils.getDeletedNodeSnapshot(record));
	}

	@Test (expected=IllegalArgumentException.class)
	public void wrongTypeGetDeletedNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Team team = new Team();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		record.setJsonClassName(DeletedNode.class.getSimpleName().toLowerCase());
		DeletedNodeSnapshotUtils.getDeletedNodeSnapshot(record);
	}

	@Test
	public void getDeletedNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		DeletedNode deletedNode = new DeletedNode();
		deletedNode.setId("syn123");
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(deletedNode));
		record.setJsonClassName(DeletedNode.class.getSimpleName().toLowerCase());
		DeletedNodeSnapshot snapshot = DeletedNodeSnapshotUtils.getDeletedNodeSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(timestamp, (Long)snapshot.getTimestamp());
		assertEquals(ObjectSnapshotUtils.convertSynapseIdToLong(deletedNode.getId()), snapshot.getId());
	}

}
