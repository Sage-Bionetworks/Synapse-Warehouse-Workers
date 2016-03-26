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
import org.sagebionetworks.repo.model.quiz.MultichoiceQuestion;
import org.sagebionetworks.repo.model.quiz.PassingRecord;
import org.sagebionetworks.repo.model.quiz.Question;
import org.sagebionetworks.repo.model.quiz.ResponseCorrectness;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.CertifiedQuizQuestionRecord;
import org.sagebionetworks.warehouse.workers.model.CertifiedQuizRecord;

public class PassingRecordSnapshotUtilsTest {

	/*
	 * isValidCertifiedQuizRecord() tests
	 */
	
	@Test
	public void validCertifiedQuizRecord() {
		CertifiedQuizRecord record = ObjectSnapshotTestUtil.createValidCertifiedQuizRecord();
		assertTrue(PassingRecordSnapshotUtils.isValidCertifiedQuizRecord(record));
	}

	@Test
	public void invalidCertifiedQuizRecordWithNullResponseId() {
		CertifiedQuizRecord record = ObjectSnapshotTestUtil.createValidCertifiedQuizRecord();
		record.setResponseId(null);
		assertFalse(PassingRecordSnapshotUtils.isValidCertifiedQuizRecord(record));
	}

	@Test
	public void invalidCertifiedQuizRecordWithNullUserId() {
		CertifiedQuizRecord record = ObjectSnapshotTestUtil.createValidCertifiedQuizRecord();
		record.setUserId(null);
		assertFalse(PassingRecordSnapshotUtils.isValidCertifiedQuizRecord(record));
	}

	@Test
	public void invalidCertifiedQuizRecordWithNullPassed() {
		CertifiedQuizRecord record = ObjectSnapshotTestUtil.createValidCertifiedQuizRecord();
		record.setPassed(null);
		assertFalse(PassingRecordSnapshotUtils.isValidCertifiedQuizRecord(record));
	}

	@Test
	public void invalidCertifiedQuizRecordWithNullPassedOn() {
		CertifiedQuizRecord record = ObjectSnapshotTestUtil.createValidCertifiedQuizRecord();
		record.setPassedOn(null);
		assertFalse(PassingRecordSnapshotUtils.isValidCertifiedQuizRecord(record));
	}

	/*
	 * getCertifiedQuizRecord() tests
	 */
	
	@Test
	public void getCertifiedQuizRecordWithNullRecord() {
		assertNull(PassingRecordSnapshotUtils.getCertifiedQuizRecord(null));
	}

	@Test
	public void getCertifiedQuizRecordWithNullTimestamp() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(null);
		record.setJsonClassName(PassingRecord.class.getSimpleName().toLowerCase());
		PassingRecord passingRecord = new PassingRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(passingRecord));
		assertNull(PassingRecordSnapshotUtils.getCertifiedQuizRecord(record));
	}

	@Test
	public void getCertifiedQuizRecordWithNullJsonString() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(PassingRecord.class.getSimpleName().toLowerCase());
		record.setJsonString(null);
		assertNull(PassingRecordSnapshotUtils.getCertifiedQuizRecord(record));
	}

	@Test
	public void getCertifiedQuizRecordWithNullJsonClassName() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(null);
		PassingRecord passingRecord = new PassingRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(passingRecord));
		assertNull(PassingRecordSnapshotUtils.getCertifiedQuizRecord(record));
	}

	@Test
	public void getCertifiedQuizRecordWithWrongJsonClassName() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(AccessRecord.class.getSimpleName().toLowerCase());
		PassingRecord passingRecord = new PassingRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(passingRecord));
		assertNull(PassingRecordSnapshotUtils.getCertifiedQuizRecord(record));
	}

	@Test
	public void getCertifiedQuizRecord() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(PassingRecord.class.getSimpleName().toLowerCase());
		Long responseId = 123L;
		Long userId = 456L;
		Long passedOn = System.currentTimeMillis();
		Boolean passed = false;
		PassingRecord passingRecord = new PassingRecord();
		passingRecord.setResponseId(responseId);
		passingRecord.setUserId(""+userId);
		passingRecord.setPassed(passed);
		passingRecord.setPassedOn(new Date(passedOn));
		record.setJsonString(EntityFactory.createJSONStringForEntity(passingRecord));
		CertifiedQuizRecord certifiedQuizRecord = PassingRecordSnapshotUtils.getCertifiedQuizRecord(record);
		assertNotNull(certifiedQuizRecord);
		assertEquals(responseId, certifiedQuizRecord.getResponseId());
		assertEquals(userId, certifiedQuizRecord.getUserId());
		assertEquals(passed, certifiedQuizRecord.getPassed());
		assertEquals(passedOn, certifiedQuizRecord.getPassedOn());
	}

	/*
	 * isValidCertifiedQuizQuestionRecord() tests
	 */
	
	@Test
	public void validCertifiedQuizQuestionRecord() {
		CertifiedQuizQuestionRecord record = ObjectSnapshotTestUtil.createValidCertifiedQuizQuestionRecord();
		assertTrue(PassingRecordSnapshotUtils.isValidCertifiedQuizQuestionRecord(record));
	}

	@Test
	public void invalidCertifiedQuizQuestionRecordWithNullResponseId() {
		CertifiedQuizQuestionRecord record = ObjectSnapshotTestUtil.createValidCertifiedQuizQuestionRecord();
		record.setResponseId(null);
		assertFalse(PassingRecordSnapshotUtils.isValidCertifiedQuizQuestionRecord(record));
	}

	@Test
	public void invalidCertifiedQuizQuestionRecordWithNullQuestionIndex() {
		CertifiedQuizQuestionRecord record = ObjectSnapshotTestUtil.createValidCertifiedQuizQuestionRecord();
		record.setQuestionIndex(null);
		assertFalse(PassingRecordSnapshotUtils.isValidCertifiedQuizQuestionRecord(record));
	}

	@Test
	public void invalidCertifiedQuizQuestionRecordWithNullIsCorrect() {
		CertifiedQuizQuestionRecord record = ObjectSnapshotTestUtil.createValidCertifiedQuizQuestionRecord();
		record.setIsCorrect(null);
		assertFalse(PassingRecordSnapshotUtils.isValidCertifiedQuizQuestionRecord(record));
	}

	/*
	 * getCertifiedQuizQuestionRecord() tests
	 */
	
	@Test
	public void getCertifiedQuizQuestionRecordWithNullRecord() {
		assertNull(PassingRecordSnapshotUtils.getCertifiedQuizQuestionRecords(null));
	}

	@Test
	public void getCertifiedQuizQuestionRecordWithNullTimestamp() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(null);
		record.setJsonClassName(PassingRecord.class.getSimpleName().toLowerCase());
		PassingRecord passingRecord = new PassingRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(passingRecord));
		assertNull(PassingRecordSnapshotUtils.getCertifiedQuizQuestionRecords(record));
	}

	@Test
	public void getCertifiedQuizQuestionRecordWithNullJsonString() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(PassingRecord.class.getSimpleName().toLowerCase());
		record.setJsonString(null);
		assertNull(PassingRecordSnapshotUtils.getCertifiedQuizQuestionRecords(record));
	}

	@Test
	public void getCertifiedQuizQuestionRecordWithNullJsonClassName() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(null);
		PassingRecord passingRecord = new PassingRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(passingRecord));
		assertNull(PassingRecordSnapshotUtils.getCertifiedQuizQuestionRecords(record));
	}

	@Test
	public void getCertifiedQuizQuestionRecordWithWrongJsonClassName() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(AccessRecord.class.getSimpleName().toLowerCase());
		PassingRecord passingRecord = new PassingRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(passingRecord));
		assertNull(PassingRecordSnapshotUtils.getCertifiedQuizQuestionRecords(record));
	}

	@Test
	public void getCertifiedQuizQuestionRecord() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(PassingRecord.class.getSimpleName().toLowerCase());
		Long responseId = 123L;
		Long questionIndex = 456L;
		Boolean isCorrect = false;
		PassingRecord passingRecord = new PassingRecord();
		passingRecord.setResponseId(responseId);
		ResponseCorrectness correctness = new ResponseCorrectness();
		Question question = new MultichoiceQuestion();
		question.setQuestionIndex(questionIndex);
		correctness.setIsCorrect(isCorrect);
		correctness.setQuestion(question);
		passingRecord.setCorrections(Arrays.asList(correctness));
		record.setJsonString(EntityFactory.createJSONStringForEntity(passingRecord));
		List<CertifiedQuizQuestionRecord> certifiedQuizRecords = PassingRecordSnapshotUtils.getCertifiedQuizQuestionRecords(record);
		assertNotNull(certifiedQuizRecords);
		assertEquals(1L, certifiedQuizRecords.size());
		CertifiedQuizQuestionRecord certifiedQuizRecord = certifiedQuizRecords.get(0);
		assertEquals(responseId, certifiedQuizRecord.getResponseId());
		assertEquals(questionIndex, certifiedQuizRecord.getQuestionIndex());
		assertEquals(isCorrect, certifiedQuizRecord.getIsCorrect());
	}

}
