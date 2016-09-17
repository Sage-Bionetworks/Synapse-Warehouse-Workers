package org.sagebionetworks.warehouse.workers.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.verification.VerificationState;
import org.sagebionetworks.repo.model.verification.VerificationStateEnum;
import org.sagebionetworks.repo.model.verification.VerificationSubmission;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionStateRecord;

public class VerificationSubmissionRecordWorkerTest {

	VerificationSubmissionStateRecordWorker worker;

	@Before
	public void before() {
		worker = new VerificationSubmissionStateRecordWorker(null, null, null);
	}

	@Test
	public void testConvertValidRecord() throws JSONObjectAdapterException {
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
		List<VerificationSubmissionStateRecord> actual = worker.convert(record);
		assertNotNull(actual);
		assertEquals(actual.size(), 1);
	}

	@Test
	public void testConvertInvalidRecord() throws JSONObjectAdapterException {
		assertNull(worker.convert(null));
	}
}
