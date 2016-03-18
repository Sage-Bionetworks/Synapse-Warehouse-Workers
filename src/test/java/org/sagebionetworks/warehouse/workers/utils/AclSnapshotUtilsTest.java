package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;

import org.junit.Test;
import org.sagebionetworks.repo.model.Node;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.sagebionetworks.repo.model.audit.AclRecord;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.AclSnapshot;

public class AclSnapshotUtilsTest {

	/*
	 * isValidAclSnapshot() tests
	 */
	@Test
	public void nullAclSnapshotTest() {
		assertFalse(AclSnapshotUtils.isValidAclSnapshot(null));
	}

	@Test
	public void validAclSnapshotTest() {
		AclSnapshot snapshot = ObjectSnapshotTestUtil.createValidAclSnapshot();
		assertTrue(AclSnapshotUtils.isValidAclSnapshot(snapshot));
	}

	@Test
	public void invalidAclSnapshotWithNullTimestampTest() {
		AclSnapshot snapshot = ObjectSnapshotTestUtil.createValidAclSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(AclSnapshotUtils.isValidAclSnapshot(snapshot));
	}

	@Test
	public void invalidAclSnapshotWithNullOwnerIdTest() {
		AclSnapshot snapshot = ObjectSnapshotTestUtil.createValidAclSnapshot();
		snapshot.setId(null);
		assertFalse(AclSnapshotUtils.isValidAclSnapshot(snapshot));
	}

	@Test
	public void invalidAclSnapshotWithNullOwnerTypeTest() {
		AclSnapshot snapshot = ObjectSnapshotTestUtil.createValidAclSnapshot();
		snapshot.setOwnerType(null);
		assertFalse(AclSnapshotUtils.isValidAclSnapshot(snapshot));
	}

	/*
	 * getAclSnapshot() tests
	 */
	@Test
	public void nullTimstampGetAclSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		AclRecord acl = new AclRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(acl));
		record.setJsonClassName(AclRecord.class.getSimpleName().toLowerCase());
		assertNull(AclSnapshotUtils.getAclSnapshot(record));
	}

	@Test
	public void nullJsonStringGetAclSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(AclRecord.class.getSimpleName().toLowerCase());
		assertNull(AclSnapshotUtils.getAclSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetAclSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		AclRecord acl = new AclRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(acl));
		assertNull(AclSnapshotUtils.getAclSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetAclSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		AclRecord acl = new AclRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(acl));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(AclSnapshotUtils.getAclSnapshot(record));
	}

	@Test
	public void wrongTypeGetAclSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(AclRecord.class.getSimpleName().toLowerCase());
		AclSnapshot snapshot = AclSnapshotUtils.getAclSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(new AclSnapshot(), snapshot);
	}

	@Test
	public void getAclSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		AclRecord acl = new AclRecord();
		acl.setCreationDate(new Date());
		acl.setId("id");
		acl.setOwnerType(ObjectType.ENTITY);
		acl.setResourceAccess(new HashSet<ResourceAccess>());
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(acl));
		record.setJsonClassName(AclRecord.class.getSimpleName().toLowerCase());
		AclSnapshot snapshot = AclSnapshotUtils.getAclSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(timestamp, snapshot.getTimestamp());
		assertEquals(acl.getCreationDate(), snapshot.getCreationDate());
		assertEquals(acl.getId(), snapshot.getId());
		assertEquals(acl.getOwnerType(), snapshot.getOwnerType());
		assertEquals(acl.getResourceAccess(), snapshot.getResourceAccess());
	}

}
