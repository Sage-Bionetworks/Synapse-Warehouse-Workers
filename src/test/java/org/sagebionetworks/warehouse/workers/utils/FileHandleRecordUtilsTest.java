package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.sagebionetworks.repo.model.Node;
import org.sagebionetworks.repo.model.audit.FileHandleSnapshot;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class FileHandleRecordUtilsTest {

	/*
	 * isValidFileHandleSnapshot() tests
	 */
	@Test
	public void nullFileHandleSnapshotTest() {
		assertFalse(FileHandleSnapshotUtils.isValidFileHandleSnapshot(null));
	}

	@Test
	public void validFileHandleSnapshotTest() {
		FileHandleSnapshot snapshot = ObjectSnapshotTestUtil.createValidFileHandleSnapshot();
		assertTrue(FileHandleSnapshotUtils.isValidFileHandleSnapshot(snapshot));
	}

	@Test
	public void invalidFileHandleSnapshotWithNullIdTest() {
		FileHandleSnapshot snapshot = ObjectSnapshotTestUtil.createValidFileHandleSnapshot();
		snapshot.setId(null);
		assertFalse(FileHandleSnapshotUtils.isValidFileHandleSnapshot(snapshot));
	}

	@Test
	public void invalidFileHandleSnapshotWithNullCreatedOnTest() {
		FileHandleSnapshot snapshot = ObjectSnapshotTestUtil.createValidFileHandleSnapshot();
		snapshot.setCreatedOn(null);
		assertFalse(FileHandleSnapshotUtils.isValidFileHandleSnapshot(snapshot));
	}

	@Test
	public void invalidFileHandleSnapshotWithNullCreatedByTest() {
		FileHandleSnapshot snapshot = ObjectSnapshotTestUtil.createValidFileHandleSnapshot();
		snapshot.setCreatedBy(null);
		assertFalse(FileHandleSnapshotUtils.isValidFileHandleSnapshot(snapshot));
	}

	@Test
	public void invalidFileHandleSnapshotWithNullConcreateType() {
		FileHandleSnapshot snapshot = ObjectSnapshotTestUtil.createValidFileHandleSnapshot();
		snapshot.setConcreteType(null);
		assertFalse(FileHandleSnapshotUtils.isValidFileHandleSnapshot(snapshot));
	}

	@Test
	public void invalidFileHandleSnapshotWithNullFileName() {
		FileHandleSnapshot snapshot = ObjectSnapshotTestUtil.createValidFileHandleSnapshot();
		snapshot.setFileName(null);
		assertFalse(FileHandleSnapshotUtils.isValidFileHandleSnapshot(snapshot));
	}

	/*
	 * getFileHandleSnapshot() tests
	 */
	@Test
	public void getFileHandleSnapshotWithNullTimestampTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileHandleSnapshot snapshot = new FileHandleSnapshot();
		record.setJsonString(EntityFactory.createJSONStringForEntity(snapshot));
		record.setJsonClassName(FileHandleSnapshot.class.getSimpleName().toLowerCase());
		assertNull(FileHandleSnapshotUtils.getFileHandleSnapshot(record));
	}

	@Test
	public void getFileHandleSnapshotWithNullJsonStringTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(FileHandleSnapshot.class.getSimpleName().toLowerCase());
		assertNull(FileHandleSnapshotUtils.getFileHandleSnapshot(record));
	}

	@Test
	public void getFileHandleSnapshotWithNullJsonClassNameTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileHandleSnapshot snapshot = new FileHandleSnapshot();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(snapshot));
		assertNull(FileHandleSnapshotUtils.getFileHandleSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetFileHandleSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileHandleSnapshot snapshot = new FileHandleSnapshot();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(snapshot));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(FileHandleSnapshotUtils.getFileHandleSnapshot(record));
	}

	@Test
	public void wrongTypeGetFileHandleSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(FileHandleSnapshot.class.getSimpleName().toLowerCase());
		FileHandleSnapshot snapshot = FileHandleSnapshotUtils.getFileHandleSnapshot(record);
		assertFalse(FileHandleSnapshotUtils.isValidFileHandleSnapshot(snapshot));
	}

	@Test
	public void getFileHandleSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileHandleSnapshot snapshot = new FileHandleSnapshot();
		snapshot.setId("123");
		snapshot.setCreatedOn(new Date());
		snapshot.setCreatedBy("999");
		snapshot.setConcreteType("concreteType");
		snapshot.setFileName("fileName");
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(snapshot));
		record.setJsonClassName(FileHandleSnapshot.class.getSimpleName().toLowerCase());
		FileHandleSnapshot result = FileHandleSnapshotUtils.getFileHandleSnapshot(record);
		assertNotNull(result);
		assertEquals(snapshot, result);
	}

}
