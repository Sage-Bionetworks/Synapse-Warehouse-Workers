package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.repo.model.Node;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.repo.model.file.FileHandleAssociation;
import org.sagebionetworks.repo.model.file.FileHandleCopyRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.FileHandleCopyRecordSnapshot;

public class FileHandleCopyRecordUtilsTest {

	/*
	 * isValidFileHandleCopyRecordSnapshot() tests
	 */
	@Test
	public void nullFileHandleCopyRecordSnapshotTest() {
		assertFalse(FileHandleCopyRecordUtils.isValidFileHandleCopyRecordSnapshot(null));
	}

	@Test
	public void validFileHandleCopyRecordSnapshotTest() {
		FileHandleCopyRecordSnapshot snapshot = ObjectSnapshotTestUtil.createValidFileHandleCopyRecordSnapshot();
		assertTrue(FileHandleCopyRecordUtils.isValidFileHandleCopyRecordSnapshot(snapshot));
	}

	@Test
	public void invalidFileHandleCopyRecordSnapshotWithNullObjectType() {
		FileHandleCopyRecordSnapshot snapshot = ObjectSnapshotTestUtil.createValidFileHandleCopyRecordSnapshot();
		snapshot.setAssociationObjectType(null);
		assertFalse(FileHandleCopyRecordUtils.isValidFileHandleCopyRecordSnapshot(snapshot));
	}

	/*
	 * getFileHandleCopyRecordSnapshot() tests
	 */
	@Test
	public void getFileHandleCopyRecordSnapshotWithNullTimestampTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileHandleCopyRecord snapshot = new FileHandleCopyRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(snapshot));
		record.setJsonClassName(FileHandleCopyRecordSnapshot.class.getSimpleName().toLowerCase());
		assertNull(FileHandleCopyRecordUtils.getFileHandleCopyRecordSnapshot(record));
	}

	@Test
	public void getFileHandleCopyRecordSnapshotWithNullJsonStringTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(FileHandleCopyRecordSnapshot.class.getSimpleName().toLowerCase());
		assertNull(FileHandleCopyRecordUtils.getFileHandleCopyRecordSnapshot(record));
	}

	@Test
	public void getFileHandleCopyRecordSnapshotWithNullJsonClassNameTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileHandleCopyRecord snapshot = new FileHandleCopyRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(snapshot));
		assertNull(FileHandleCopyRecordUtils.getFileHandleCopyRecordSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetFileHandleCopyRecordSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileHandleCopyRecord snapshot = new FileHandleCopyRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(snapshot));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(FileHandleCopyRecordUtils.getFileHandleCopyRecordSnapshot(record));
	}

	@Test
	public void wrongTypeGetFileHandleCopyRecordSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(FileHandleCopyRecordSnapshot.class.getSimpleName().toLowerCase());
		FileHandleCopyRecordSnapshot snapshot = FileHandleCopyRecordUtils.getFileHandleCopyRecordSnapshot(record);
		assertFalse(FileHandleCopyRecordUtils.isValidFileHandleCopyRecordSnapshot(snapshot));
	}

	@Test
	public void getFileHandleCopyRecordSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileHandleCopyRecord copyRecord = new FileHandleCopyRecord();
		copyRecord.setUserId("1");
		copyRecord.setNewFileHandleId("3");
		FileHandleAssociation originalFileHandle = new FileHandleAssociation();
		originalFileHandle.setFileHandleId("2");
		originalFileHandle.setAssociateObjectId("4");
		originalFileHandle.setAssociateObjectType(FileHandleAssociateType.FileEntity);
		copyRecord.setOriginalFileHandle(originalFileHandle );
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(copyRecord));
		record.setJsonClassName(FileHandleCopyRecord.class.getSimpleName().toLowerCase());
		FileHandleCopyRecordSnapshot result = FileHandleCopyRecordUtils.getFileHandleCopyRecordSnapshot(record);
		assertNotNull(result);
		FileHandleCopyRecordSnapshot snapshot = new FileHandleCopyRecordSnapshot();
		snapshot.setTimestamp(timestamp);
		snapshot.setUserId(1L);
		snapshot.setOriginalFileHandleId(2L);
		snapshot.setNewFileHandleId(3L);
		snapshot.setAssociationObjectId(4L);
		snapshot.setAssociationObjectType(FileHandleAssociateType.FileEntity);
		assertEquals(snapshot , result);
	}

}
