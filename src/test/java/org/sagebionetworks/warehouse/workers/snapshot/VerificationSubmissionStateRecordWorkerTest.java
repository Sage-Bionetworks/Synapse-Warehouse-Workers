package org.sagebionetworks.warehouse.workers.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.verification.VerificationSubmission;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionRecord;

public class VerificationSubmissionStateRecordWorkerTest {

	VerificationSubmissionRecordWorker worker;

	@Before
	public void before() {
		worker = new VerificationSubmissionRecordWorker(null, null, null, null);
	}

	@Test
	public void testConvertValidRecord() throws JSONObjectAdapterException {
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
		List<VerificationSubmissionRecord> actual = worker.convert(record);
		assertNotNull(actual);
		assertEquals(actual.size(), 1);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvertInvalidRecord() throws JSONObjectAdapterException {
		worker.convert(null);
	}
}
