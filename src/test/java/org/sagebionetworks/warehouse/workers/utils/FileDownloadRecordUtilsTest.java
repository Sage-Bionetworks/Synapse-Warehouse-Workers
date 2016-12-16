package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.sagebionetworks.repo.model.Node;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.file.BulkFileDownloadResponse;
import org.sagebionetworks.repo.model.file.FileDownloadRecord;
import org.sagebionetworks.repo.model.file.FileDownloadStatus;
import org.sagebionetworks.repo.model.file.FileDownloadSummary;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.repo.model.file.FileHandleAssociation;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.FileDownload;
import org.sagebionetworks.warehouse.workers.model.FileHandleDownload;

public class FileDownloadRecordUtilsTest {

	/*
	 * isValidFileDownloadRecord() tests
	 */
	@Test
	public void nullFileDownloadTest() {
		assertFalse(FileDownloadRecordUtils.isValidFileDownloadRecord(null));
	}

	@Test
	public void validFileDownloadTest() {
		FileDownload snapshot = ObjectSnapshotTestUtil.createValidFileDownloadRecord();
		assertTrue(FileDownloadRecordUtils.isValidFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileDownloadWithNullTimestampTest() {
		FileDownload snapshot = ObjectSnapshotTestUtil.createValidFileDownloadRecord();
		snapshot.setTimestamp(null);
		assertFalse(FileDownloadRecordUtils.isValidFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileDownloadWithNullUserIdTest() {
		FileDownload snapshot = ObjectSnapshotTestUtil.createValidFileDownloadRecord();
		snapshot.setUserId(null);
		assertFalse(FileDownloadRecordUtils.isValidFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileDownloadWithNullFileHandleIdTest() {
		FileDownload snapshot = ObjectSnapshotTestUtil.createValidFileDownloadRecord();
		snapshot.setFileHandleId(null);
		assertFalse(FileDownloadRecordUtils.isValidFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileDownloadWithNullObjectIdTest() {
		FileDownload snapshot = ObjectSnapshotTestUtil.createValidFileDownloadRecord();
		snapshot.setAssociationObjectId(null);
		assertFalse(FileDownloadRecordUtils.isValidFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileDownloadWithNullObjectTypeTest() {
		FileDownload snapshot = ObjectSnapshotTestUtil.createValidFileDownloadRecord();
		snapshot.setAssociationObjectType(null);
		assertFalse(FileDownloadRecordUtils.isValidFileDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileDownloadsWithNullObject() {
		assertFalse(FileDownloadRecordUtils.isValidFileDownloadRecords(null));
	}

	/*
	 * getFileDownloadRecordsForBulkDownloadRecord() tests
	 */
	@Test
	public void getFileDownloadRecordsForBulkDownloadRecordWithNullTimestampTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		assertNull(FileDownloadRecordUtils.getFileDownloadRecordsForBulkFileDownloadRecord(record));
	}

	@Test
	public void getFileDownloadRecordsForBulkDownloadRecordWithNullJsonStringTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		assertNull(FileDownloadRecordUtils.getFileDownloadRecordsForBulkFileDownloadRecord(record));
	}

	@Test
	public void getFileDownloadRecordsForBulkDownloadRecordWithNullJsonClassNameTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		assertNull(FileDownloadRecordUtils.getFileDownloadRecordsForBulkFileDownloadRecord(record));
	}

	@Test
	public void wrongTypeNameGetFileDownloadRecordsForBulkDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(FileDownloadRecordUtils.getFileDownloadRecordsForBulkFileDownloadRecord(record));
	}

	@Test (expected = NullPointerException.class)
	public void wrongTypeGetFileDownloadRecordsForBulkDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		FileDownloadRecordUtils.getFileDownloadRecordsForBulkFileDownloadRecord(record);
	}

	@Test
	public void getFileDownloadRecordsForBulkDownloadRecordTest() throws JSONObjectAdapterException {
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
		List<FileDownload> records = FileDownloadRecordUtils.getFileDownloadRecordsForBulkFileDownloadRecord(record);
		assertNotNull(records);
		assertEquals(3, records.size());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));
		assertTrue(records.contains(record3));
	}

	/*
	 * isValidFileHandleDownloadRecord() tests
	 */
	@Test
	public void nullFileHandleDownloadTest() {
		assertFalse(FileDownloadRecordUtils.isValidFileHandleDownloadRecord(null));
	}

	@Test
	public void validFileHandleDownloadTest() {
		FileHandleDownload snapshot = ObjectSnapshotTestUtil.createValidFileHandleDownloadRecord();
		assertTrue(FileDownloadRecordUtils.isValidFileHandleDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileHandleDownloadWithNullTimestampTest() {
		FileHandleDownload snapshot = ObjectSnapshotTestUtil.createValidFileHandleDownloadRecord();
		snapshot.setTimestamp(null);
		assertFalse(FileDownloadRecordUtils.isValidFileHandleDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileHandleDownloadWithNullUserIdTest() {
		FileHandleDownload snapshot = ObjectSnapshotTestUtil.createValidFileHandleDownloadRecord();
		snapshot.setUserId(null);
		assertFalse(FileDownloadRecordUtils.isValidFileHandleDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileHandleDownloadWithNullDownloadedFileHandleIdTest() {
		FileHandleDownload snapshot = ObjectSnapshotTestUtil.createValidFileHandleDownloadRecord();
		snapshot.setDownloadedFileHandleId(null);
		assertFalse(FileDownloadRecordUtils.isValidFileHandleDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileHandleDownloadWithNullRequestedFileHandleIdTest() {
		FileHandleDownload snapshot = ObjectSnapshotTestUtil.createValidFileHandleDownloadRecord();
		snapshot.setRequestedFileHandleId(null);
		assertFalse(FileDownloadRecordUtils.isValidFileHandleDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileHandleDownloadWithNullObjectIdTest() {
		FileHandleDownload snapshot = ObjectSnapshotTestUtil.createValidFileHandleDownloadRecord();
		snapshot.setAssociationObjectId(null);
		assertFalse(FileDownloadRecordUtils.isValidFileHandleDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileHandleDownloadWithNullObjectTypeTest() {
		FileHandleDownload snapshot = ObjectSnapshotTestUtil.createValidFileHandleDownloadRecord();
		snapshot.setAssociationObjectType(null);
		assertFalse(FileDownloadRecordUtils.isValidFileHandleDownloadRecord(snapshot));
	}

	@Test
	public void invalidFileHandleDownloadsWithNullObject() {
		assertFalse(FileDownloadRecordUtils.isValidFileHandleDownloadRecords(null));
	}

	/*
	 * getFileHandleDownloadRecordsForBulkFileDownloadRecord() tests
	 */
	@Test
	public void getFileHandleDownloadRecordsForBulkFileDownloadRecordWithNullTimestampTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		assertNull(FileDownloadRecordUtils.getFileHandleDownloadRecordsForBulkFileDownloadRecord(record));
	}

	@Test
	public void getFileHandleDownloadRecordsForBulkFileDownloadRecordWithNullJsonStringTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		assertNull(FileDownloadRecordUtils.getFileHandleDownloadRecordsForBulkFileDownloadRecord(record));
	}

	@Test
	public void getFileHandleDownloadRecordsForBulkFileDownloadRecordWithNullJsonClassNameTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		assertNull(FileDownloadRecordUtils.getFileHandleDownloadRecordsForBulkFileDownloadRecord(record));
	}

	@Test
	public void wrongTypeNameGetFileHandleDownloadRecordsForBulkFileDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(FileDownloadRecordUtils.getFileHandleDownloadRecordsForBulkFileDownloadRecord(record));
	}

	@Test (expected = NullPointerException.class)
	public void wrongTypeGetFileHandleDownloadRecordsForBulkFileDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		FileDownloadRecordUtils.getFileHandleDownloadRecordsForBulkFileDownloadRecord(record);
	}

	@Test
	public void getFileHandleDownloadRecordsForBulkFileDownloadRecordTest() throws JSONObjectAdapterException {
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
		response.setResultZipFileHandleId("666");
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		FileHandleDownload record1 = new FileHandleDownload();
		record1.setTimestamp(timestamp);
		record1.setUserId(123L);
		record1.setDownloadedFileHandleId(666L);
		record1.setRequestedFileHandleId(999L);
		record1.setAssociationObjectId(456L);
		record1.setAssociationObjectType(FileHandleAssociateType.TableEntity);
		FileHandleDownload record2 = new FileHandleDownload();
		record2.setTimestamp(timestamp);
		record2.setUserId(123L);
		record2.setDownloadedFileHandleId(666L);
		record2.setRequestedFileHandleId(222L);
		record2.setAssociationObjectId(789L);
		record2.setAssociationObjectType(FileHandleAssociateType.TableEntity);
		FileHandleDownload record3 = new FileHandleDownload();
		record3.setTimestamp(timestamp);
		record3.setUserId(123L);
		record3.setDownloadedFileHandleId(666L);
		record3.setRequestedFileHandleId(111L);
		record3.setAssociationObjectId(456L);
		record3.setAssociationObjectType(FileHandleAssociateType.TableEntity);
		List<FileHandleDownload> records = FileDownloadRecordUtils.getFileHandleDownloadRecordsForBulkFileDownloadRecord(record);
		assertNotNull(records);
		assertEquals(3, records.size());
		assertTrue(records.contains(record1));
		assertTrue(records.contains(record2));
		assertTrue(records.contains(record3));
	}

	/*
	 * getFileDownloadRecordsForDownloadRecord() tests
	 */
	@Test
	public void getFileDownloadRecordsForDownloadRecordWithNullTimestampTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileDownloadRecord fdrecord = new FileDownloadRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(fdrecord));
		record.setJsonClassName(FileDownloadRecord.class.getSimpleName().toLowerCase());
		assertNull(FileDownloadRecordUtils.getFileDownloadRecordsForFileDownloadRecord(record));
	}

	@Test
	public void getFileDownloadRecordsForDownloadRecordWithNullJsonStringTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(FileDownloadRecord.class.getSimpleName().toLowerCase());
		assertNull(FileDownloadRecordUtils.getFileDownloadRecordsForFileDownloadRecord(record));
	}

	@Test
	public void getFileDownloadRecordsForDownloadRecordWithNullJsonClassNameTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileDownloadRecord fdrecord = new FileDownloadRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(fdrecord));
		assertNull(FileDownloadRecordUtils.getFileDownloadRecordsForFileDownloadRecord(record));
	}

	@Test
	public void wrongTypeNameGetFileDownloadRecordsForDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileDownloadRecord fdrecord = new FileDownloadRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(fdrecord));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(FileDownloadRecordUtils.getFileDownloadRecordsForFileDownloadRecord(record));
	}

	@Test (expected = RuntimeException.class)
	public void wrongTypeGetFileDownloadRecordsForDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(FileDownloadRecord.class.getSimpleName().toLowerCase());
		FileDownloadRecordUtils.getFileDownloadRecordsForFileDownloadRecord(record);
	}

	@Test
	public void getFileDownloadRecordsForDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileDownloadRecord fdrecord = new FileDownloadRecord();
		fdrecord.setUserId("123");
		FileHandleAssociation fha = new FileHandleAssociation();
		fha.setAssociateObjectId("syn456");
		fha.setAssociateObjectType(FileHandleAssociateType.TableEntity);
		fha.setFileHandleId("999");
		fdrecord.setDownloadedFile(fha);
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(fdrecord));
		record.setJsonClassName(FileDownloadRecord.class.getSimpleName().toLowerCase());
		FileDownload fd = new FileDownload();
		fd.setTimestamp(timestamp);
		fd.setUserId(123L);
		fd.setFileHandleId(999L);
		fd.setAssociationObjectId(456L);
		fd.setAssociationObjectType(FileHandleAssociateType.TableEntity);
		List<FileDownload> records = FileDownloadRecordUtils.getFileDownloadRecordsForFileDownloadRecord(record);
		assertNotNull(records);
		assertEquals(1, records.size());
		assertTrue(records.contains(fd));
	}


	/*
	 * getFileHandleDownloadRecordsForFileDownloadRecord() tests
	 */
	@Test
	public void getFileHandleDownloadRecordsForFileDownloadRecordWithNullTimestampTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileDownloadRecord fdrecord = new FileDownloadRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(fdrecord));
		record.setJsonClassName(FileDownloadRecord.class.getSimpleName().toLowerCase());
		assertNull(FileDownloadRecordUtils.getFileHandleDownloadRecordsForFileDownloadRecord(record));
	}

	@Test
	public void getFileHandleDownloadRecordsForFileDownloadRecordWithNullJsonStringTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(FileDownloadRecord.class.getSimpleName().toLowerCase());
		assertNull(FileDownloadRecordUtils.getFileHandleDownloadRecordsForFileDownloadRecord(record));
	}

	@Test
	public void getFileHandleDownloadRecordsForFileDownloadRecordWithNullJsonClassNameTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileDownloadRecord fdrecord = new FileDownloadRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(fdrecord));
		assertNull(FileDownloadRecordUtils.getFileHandleDownloadRecordsForFileDownloadRecord(record));
	}

	@Test
	public void wrongTypeNameGetFileHandleDownloadRecordsForFileDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileDownloadRecord fdrecord = new FileDownloadRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(fdrecord));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(FileDownloadRecordUtils.getFileHandleDownloadRecordsForFileDownloadRecord(record));
	}

	@Test (expected = RuntimeException.class)
	public void wrongTypeGetFileHandleDownloadRecordsForFileDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(FileDownloadRecord.class.getSimpleName().toLowerCase());
		FileDownloadRecordUtils.getFileHandleDownloadRecordsForFileDownloadRecord(record);
	}

	@Test
	public void getFileHandleDownloadRecordsForFileDownloadRecordTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileDownloadRecord fdrecord = new FileDownloadRecord();
		fdrecord.setUserId("123");
		FileHandleAssociation fha = new FileHandleAssociation();
		fha.setAssociateObjectId("syn456");
		fha.setAssociateObjectType(FileHandleAssociateType.TableEntity);
		fha.setFileHandleId("999");
		fdrecord.setDownloadedFile(fha);
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(fdrecord));
		record.setJsonClassName(FileDownloadRecord.class.getSimpleName().toLowerCase());
		FileHandleDownload fhd = new FileHandleDownload();
		fhd.setTimestamp(timestamp);
		fhd.setUserId(123L);
		fhd.setRequestedFileHandleId(999L);
		fhd.setDownloadedFileHandleId(999L);
		fhd.setAssociationObjectId(456L);
		fhd.setAssociationObjectType(FileHandleAssociateType.TableEntity);
		List<FileHandleDownload> records = FileDownloadRecordUtils.getFileHandleDownloadRecordsForFileDownloadRecord(record);
		assertNotNull(records);
		assertEquals(1, records.size());
		assertTrue(records.contains(fhd));
	}
}
