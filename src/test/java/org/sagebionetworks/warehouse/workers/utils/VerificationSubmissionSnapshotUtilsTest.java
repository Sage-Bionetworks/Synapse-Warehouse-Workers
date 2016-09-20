package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.verification.VerificationState;
import org.sagebionetworks.repo.model.verification.VerificationStateEnum;
import org.sagebionetworks.repo.model.verification.VerificationSubmission;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionRecord;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionStateRecord;

public class VerificationSubmissionSnapshotUtilsTest {

	/*
	 * isValidVerificationSubmissionRecord() tests
	 */
	
	@Test
	public void validVerificationSubmissionRecord() {
		VerificationSubmissionRecord record = ObjectSnapshotTestUtil.createValidVerificationSubmissionRecord();
		assertTrue(VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionRecord(record));
	}

	@Test
	public void invalidVerificationSubmissionRecordWithNull() {
		assertFalse(VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionRecord(null));
	}

	@Test
	public void invalidVerificationSubmissionRecordWithNullId() {
		VerificationSubmissionRecord record = ObjectSnapshotTestUtil.createValidVerificationSubmissionRecord();
		record.setId(null);
		assertFalse(VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionRecord(record));
	}

	@Test
	public void invalidVerificationSubmissionRecordWithNullCreatedOn() {
		VerificationSubmissionRecord record = ObjectSnapshotTestUtil.createValidVerificationSubmissionRecord();
		record.setCreatedOn(null);
		assertFalse(VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionRecord(record));
	}

	@Test
	public void invalidVerificationSubmissionRecordWithNullCreatedBy() {
		VerificationSubmissionRecord record = ObjectSnapshotTestUtil.createValidVerificationSubmissionRecord();
		record.setCreatedBy(null);
		assertFalse(VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionRecord(record));
	}

	/*
	 * getVerificationSubmissionRecord() tests
	 */
	
	@Test
	public void getVerificationSubmissionRecordWithNullRecord() {
		assertNull(VerificationSubmissionSnapshotUtils.getVerificationSubmissionRecord(null));
	}

	@Test
	public void getVerificationSubmissionRecordWithNullTimestamp() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(null);
		record.setJsonClassName(VerificationSubmission.class.getSimpleName().toLowerCase());
		VerificationSubmission submission = new VerificationSubmission();
		record.setJsonString(EntityFactory.createJSONStringForEntity(submission));
		assertNull(VerificationSubmissionSnapshotUtils.getVerificationSubmissionRecord(record));
	}

	@Test
	public void getVerificationSubmissionRecordWithNullJsonString() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(VerificationSubmission.class.getSimpleName().toLowerCase());
		record.setJsonString(null);
		assertNull(VerificationSubmissionSnapshotUtils.getVerificationSubmissionRecord(record));
	}

	@Test
	public void getVerificationSubmissionRecordWithNullJsonClassName() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(null);
		VerificationSubmission submission = new VerificationSubmission();
		record.setJsonString(EntityFactory.createJSONStringForEntity(submission));
		assertNull(VerificationSubmissionSnapshotUtils.getVerificationSubmissionRecord(record));
	}

	@Test
	public void getVerificationSubmissionRecordWithWrongJsonClassName() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(AccessRecord.class.getSimpleName().toLowerCase());
		VerificationSubmission submission = new VerificationSubmission();
		record.setJsonString(EntityFactory.createJSONStringForEntity(submission));
		assertNull(VerificationSubmissionSnapshotUtils.getVerificationSubmissionRecord(record));
	}

	@Test
	public void getVerificationSubmissionRecord() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(VerificationSubmission.class.getSimpleName().toLowerCase());
		Long id = 123L;
		Long createdBy = 456L;
		Long createdOn = System.currentTimeMillis();
		VerificationSubmission submission = new VerificationSubmission();
		submission.setId(""+id);
		submission.setCreatedBy(""+createdBy);
		submission.setCreatedOn(new Date(createdOn));
		record.setJsonString(EntityFactory.createJSONStringForEntity(submission));
		VerificationSubmissionRecord verificationSubmissionRecord = VerificationSubmissionSnapshotUtils.getVerificationSubmissionRecord(record);
		assertNotNull(verificationSubmissionRecord);
		assertEquals(id, verificationSubmissionRecord.getId());
		assertEquals(createdBy, verificationSubmissionRecord.getCreatedBy());
		assertEquals(createdOn, verificationSubmissionRecord.getCreatedOn());
	}

	/*
	 * isValidVerificationSubmissionStateRecord() tests
	 */
	
	@Test
	public void validVerificationSubmissionStateRecord() {
		VerificationSubmissionStateRecord record = ObjectSnapshotTestUtil.createValidVerificationSubmissionStateRecord();
		assertTrue(VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionStateRecord(record));
	}

	@Test
	public void invalidVerificationSubmissionStateRecordWithNullId() {
		VerificationSubmissionStateRecord record = ObjectSnapshotTestUtil.createValidVerificationSubmissionStateRecord();
		record.setId(null);
		assertFalse(VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionStateRecord(record));
	}

	@Test
	public void invalidVerificationSubmissionStateRecordWithNullCreatedOn() {
		VerificationSubmissionStateRecord record = ObjectSnapshotTestUtil.createValidVerificationSubmissionStateRecord();
		record.setCreatedOn(null);
		assertFalse(VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionStateRecord(record));
	}

	@Test
	public void invalidVerificationSubmissionStateRecordWithNullCreatedBy() {
		VerificationSubmissionStateRecord record = ObjectSnapshotTestUtil.createValidVerificationSubmissionStateRecord();
		record.setCreatedBy(null);
		assertFalse(VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionStateRecord(record));
	}

	@Test
	public void invalidVerificationSubmissionStateRecordWithNullState() {
		VerificationSubmissionStateRecord record = ObjectSnapshotTestUtil.createValidVerificationSubmissionStateRecord();
		record.setState(null);
		assertFalse(VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionStateRecord(record));
	}

	@Test
	public void invalidVerificationSubmissionStateRecordsWithNull() {
		assertFalse(VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionStateRecords(null));
	}

	/*
	 * getVerificationSubmissionStateRecords() tests
	 */
	
	@Test
	public void getVerificationSubmissionStateRecordWithNullRecord() {
		assertNull(VerificationSubmissionSnapshotUtils.getVerificationSubmissionStateRecords(null));
	}

	@Test
	public void getVerificationSubmissionStateRecordWithNullTimestamp() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(null);
		record.setJsonClassName(VerificationSubmission.class.getSimpleName().toLowerCase());
		VerificationSubmission submission = new VerificationSubmission();
		record.setJsonString(EntityFactory.createJSONStringForEntity(submission));
		assertNull(VerificationSubmissionSnapshotUtils.getVerificationSubmissionStateRecords(record));
	}

	@Test
	public void getVerificationSubmissionStateRecordWithNullJsonString() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(VerificationSubmission.class.getSimpleName().toLowerCase());
		record.setJsonString(null);
		assertNull(VerificationSubmissionSnapshotUtils.getVerificationSubmissionStateRecords(record));
	}

	@Test
	public void getVerificationSubmissionStateRecordWithNullJsonClassName() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(null);
		VerificationSubmission submission = new VerificationSubmission();
		record.setJsonString(EntityFactory.createJSONStringForEntity(submission));
		assertNull(VerificationSubmissionSnapshotUtils.getVerificationSubmissionStateRecords(record));
	}

	@Test
	public void getVerificationSubmissionStateRecordWithWrongJsonClassName() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(AccessRecord.class.getSimpleName().toLowerCase());
		VerificationSubmission submission = new VerificationSubmission();
		record.setJsonString(EntityFactory.createJSONStringForEntity(submission));
		assertNull(VerificationSubmissionSnapshotUtils.getVerificationSubmissionStateRecords(record));
	}

	@Test
	public void getVerificationSubmissionStateRecord() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(VerificationSubmission.class.getSimpleName().toLowerCase());
		Long id = 123L;
		Long createdBy = 456L;
		Date createdOn = new Date();
		VerificationStateEnum state = VerificationStateEnum.SUBMITTED;
		VerificationSubmission submission = new VerificationSubmission();
		submission.setId(""+id);
		VerificationState verificationState = new VerificationState();
		verificationState.setCreatedOn(createdOn);
		verificationState.setCreatedBy(""+createdBy);
		verificationState.setState(state);
		submission.setStateHistory(Arrays.asList(verificationState));
		record.setJsonString(EntityFactory.createJSONStringForEntity(submission));
		List<VerificationSubmissionStateRecord> verificationSubmissionStateRecords = VerificationSubmissionSnapshotUtils.getVerificationSubmissionStateRecords(record);
		assertNotNull(verificationSubmissionStateRecords);
		assertEquals(1L, verificationSubmissionStateRecords.size());
		VerificationSubmissionStateRecord VerificationSubmissionStateRecord = verificationSubmissionStateRecords.get(0);
		assertEquals(id, VerificationSubmissionStateRecord.getId());
		assertEquals(createdBy, VerificationSubmissionStateRecord.getCreatedBy());
		assertEquals((Long)createdOn.getTime(), VerificationSubmissionStateRecord.getCreatedOn());
		assertEquals(state, VerificationSubmissionStateRecord.getState());
	}

}
