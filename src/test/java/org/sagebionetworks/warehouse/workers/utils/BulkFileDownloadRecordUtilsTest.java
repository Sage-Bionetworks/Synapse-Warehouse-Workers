package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.sagebionetworks.repo.model.Node;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.file.BulkFileDownloadResponse;
import org.sagebionetworks.repo.model.file.FileDownloadStatus;
import org.sagebionetworks.repo.model.file.FileDownloadSummary;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.BulkFileDownloadRecord;

public class BulkFileDownloadRecordUtilsTest {

	/*
	 * isValidBulkFileDownloadRecord() tests
	 */
	@Test
	public void nullBulkFileDownloadRecordTest() {
		assertFalse(BulkFileDownloadRecordUtils.isValidBulkFileDownloadRecord(null));
	}

	@Test
	public void validBulkFileDownloadRecordTest() {
		BulkFileDownloadRecord snapshot = ObjectSnapshotTestUtil.createValidBulkFileDownloadRecord();
		assertTrue(BulkFileDownloadRecordUtils.isValidBulkFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidBulkFileDownloadRecordWithNullUserIdTest() {
		BulkFileDownloadRecord snapshot = ObjectSnapshotTestUtil.createValidBulkFileDownloadRecord();
		snapshot.setUserId(null);
		assertFalse(BulkFileDownloadRecordUtils.isValidBulkFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidBulkFileDownloadRecordWithNullObjectIdTest() {
		BulkFileDownloadRecord snapshot = ObjectSnapshotTestUtil.createValidBulkFileDownloadRecord();
		snapshot.setObjectId(null);
		assertFalse(BulkFileDownloadRecordUtils.isValidBulkFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidBulkFileDownloadRecordWithNullObjectTypeTest() {
		BulkFileDownloadRecord snapshot = ObjectSnapshotTestUtil.createValidBulkFileDownloadRecord();
		snapshot.setObjectType(null);
		assertFalse(BulkFileDownloadRecordUtils.isValidBulkFileDownloadRecord(snapshot));
	}

	/*
	 * getBulkFileDownloadRecords() tests
	 */
	@Test
	public void getBulkFileDownloadRecordsWithNullTimestampTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		assertNull(BulkFileDownloadRecordUtils.getBulkFileDownloadRecords(record));
	}

	@Test
	public void getBulkFileDownloadRecordWithNullJsonStringTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		assertNull(BulkFileDownloadRecordUtils.getBulkFileDownloadRecords(record));
	}

	@Test
	public void getBulkFileDownloadRecordWithNullJsonClassNameTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		assertNull(BulkFileDownloadRecordUtils.getBulkFileDownloadRecords(record));
	}

	@Test
	public void wrongTypeNameGetBulkFileDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(BulkFileDownloadRecordUtils.getBulkFileDownloadRecords(record));
	}

	@Test (expected = NullPointerException.class)
	public void wrongTypeGetBulkFileDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		BulkFileDownloadRecordUtils.getBulkFileDownloadRecords(record);
	}

	@Test
	public void getBulkFileDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		response.setUserId("123");
		FileDownloadSummary fileDownloadSummary1 = new FileDownloadSummary();
		fileDownloadSummary1.setAssociateObjectId("syn456");
		fileDownloadSummary1.setAssociateObjectType(FileHandleAssociateType.TableEntity);
		fileDownloadSummary1.setFileHandleId("999");
		fileDownloadSummary1.setStatus(FileDownloadStatus.SUCCESS);
		FileDownloadSummary fileDownloadSummary2 = new FileDownloadSummary();
		fileDownloadSummary2.setAssociateObjectId("syn789");
		fileDownloadSummary2.setAssociateObjectType(FileHandleAssociateType.TableEntity);
		fileDownloadSummary2.setStatus(FileDownloadStatus.SUCCESS);
		// same object as record 1
		FileDownloadSummary fileDownloadSummary3 = new FileDownloadSummary();
		fileDownloadSummary3.setAssociateObjectId("syn456");
		fileDownloadSummary3.setAssociateObjectType(FileHandleAssociateType.TableEntity);
		fileDownloadSummary3.setFileHandleId("111");
		fileDownloadSummary3.setStatus(FileDownloadStatus.SUCCESS);
		// will not add this record because it's a failure
		FileDownloadSummary fileDownloadSummary4 = new FileDownloadSummary();
		fileDownloadSummary4.setAssociateObjectId("syn333");
		fileDownloadSummary4.setAssociateObjectType(FileHandleAssociateType.TableEntity);
		fileDownloadSummary4.setFileHandleId("444");
		fileDownloadSummary4.setStatus(FileDownloadStatus.FAILURE);
		List<FileDownloadSummary> fileDownloadSummaryList = Arrays.asList(fileDownloadSummary1, fileDownloadSummary2, fileDownloadSummary3, fileDownloadSummary4);
		response.setFileSummary(fileDownloadSummaryList);
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		BulkFileDownloadRecord record1 = new BulkFileDownloadRecord();
		record1.setUserId(123L);
		record1.setObjectId(456L);
		record1.setObjectType(FileHandleAssociateType.TableEntity);
		BulkFileDownloadRecord record2 = new BulkFileDownloadRecord();
		record2.setUserId(123L);
		record2.setObjectId(789L);
		record2.setObjectType(FileHandleAssociateType.TableEntity);
		List<BulkFileDownloadRecord> records = BulkFileDownloadRecordUtils.getBulkFileDownloadRecords(record);
		assertNotNull(records);
		assertEquals(2, records.size());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));
	}

}
