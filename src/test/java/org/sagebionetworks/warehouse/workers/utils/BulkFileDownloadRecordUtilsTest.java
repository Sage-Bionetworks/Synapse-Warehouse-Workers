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
import org.sagebionetworks.warehouse.workers.model.FileDownload;

public class BulkFileDownloadRecordUtilsTest {

	/*
	 * isValidFileDownloadRecord() tests
	 */
	@Test
	public void nullFileDownloadTest() {
		assertFalse(BulkFileDownloadRecordUtils.isValidFileDownloadRecord(null));
	}

	@Test
	public void validFileDownloadTest() {
		FileDownload snapshot = ObjectSnapshotTestUtil.createValidFileDownloadRecord();
		assertTrue(BulkFileDownloadRecordUtils.isValidFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileDownloadWithNullUserIdTest() {
		FileDownload snapshot = ObjectSnapshotTestUtil.createValidFileDownloadRecord();
		snapshot.setUserId(null);
		assertFalse(BulkFileDownloadRecordUtils.isValidFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileDownloadWithNullObjectIdTest() {
		FileDownload snapshot = ObjectSnapshotTestUtil.createValidFileDownloadRecord();
		snapshot.setAssociationObjectId(null);
		assertFalse(BulkFileDownloadRecordUtils.isValidFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileDownloadWithNullObjectTypeTest() {
		FileDownload snapshot = ObjectSnapshotTestUtil.createValidFileDownloadRecord();
		snapshot.setAssociationObjectType(null);
		assertFalse(BulkFileDownloadRecordUtils.isValidFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileDownloadsWithNullObject() {
		assertFalse(BulkFileDownloadRecordUtils.isValidFileDownloadRecords(null));
	}

	/*
	 * getFileDownloadRecords() tests
	 */
	@Test
	public void getFileDownloadRecordsWithNullTimestampTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		assertNull(BulkFileDownloadRecordUtils.getFileDownloadRecords(record));
	}

	@Test
	public void getFileDownloadWithNullJsonStringTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		assertNull(BulkFileDownloadRecordUtils.getFileDownloadRecords(record));
	}

	@Test
	public void getFileDownloadWithNullJsonClassNameTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		assertNull(BulkFileDownloadRecordUtils.getFileDownloadRecords(record));
	}

	@Test
	public void wrongTypeNameGetFileDownloadTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(BulkFileDownloadRecordUtils.getFileDownloadRecords(record));
	}

	@Test (expected = NullPointerException.class)
	public void wrongTypeGetFileDownloadTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		BulkFileDownloadRecordUtils.getFileDownloadRecords(record);
	}

	@Test
	public void getFileDownloadTest() throws JSONObjectAdapterException {
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
		fileDownloadSummary2.setFileHandleId("222");
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
		FileDownload record1 = new FileDownload();
		record1.setTimestamp(timestamp);
		record1.setUserId(123L);
		record1.setFileHandleId(999L);
		record1.setAssociationObjectId(456L);
		record1.setAssociationObjectType(FileHandleAssociateType.TableEntity);
		FileDownload record2 = new FileDownload();
		record2.setTimestamp(timestamp);
		record2.setUserId(123L);
		record2.setFileHandleId(222L);
		record2.setAssociationObjectId(789L);
		record2.setAssociationObjectType(FileHandleAssociateType.TableEntity);
		FileDownload record3 = new FileDownload();
		record3.setTimestamp(timestamp);
		record3.setUserId(123L);
		record3.setFileHandleId(111L);
		record3.setAssociationObjectId(456L);
		record3.setAssociationObjectType(FileHandleAssociateType.TableEntity);
		List<FileDownload> records = BulkFileDownloadRecordUtils.getFileDownloadRecords(record);
		assertNotNull(records);
		assertEquals(3, records.size());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));
		assertTrue(records.contains(record3));
	}

}
