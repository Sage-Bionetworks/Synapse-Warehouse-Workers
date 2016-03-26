package org.sagebionetworks.warehouse.workers.utils;

import java.util.ArrayList;
import java.util.List;

import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.verification.VerificationState;
import org.sagebionetworks.repo.model.verification.VerificationSubmission;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionRecord;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionStateRecord;

public class VerificationSubmissionSnapshotUtils {

	public static VerificationSubmissionRecord getVerificationSubmissionRecord(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(VerificationSubmission.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			VerificationSubmission submission = EntityFactory.createEntityFromJSONString(record.getJsonString(), VerificationSubmission.class);
			VerificationSubmissionRecord submissionRecord = new VerificationSubmissionRecord();
			submissionRecord.setId(Long.parseLong(submission.getId()));
			submissionRecord.setCreatedOn(submission.getCreatedOn().getTime());
			submissionRecord.setCreatedBy(Long.parseLong(submission.getCreatedBy()));
			return submissionRecord;
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isValidVerificationSubmissionRecord(VerificationSubmissionRecord record) {
		if (record.getId() 			== null) return false;
		if (record.getCreatedOn() 	== null) return false;
		if (record.getCreatedBy() 	== null) return false;
		return true;
	}

	public static List<VerificationSubmissionStateRecord> getVerificationSubmissionStateRecords(
			ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(VerificationSubmission.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			List<VerificationSubmissionStateRecord> records = new ArrayList<VerificationSubmissionStateRecord>();
			VerificationSubmission submission = EntityFactory.createEntityFromJSONString(record.getJsonString(), VerificationSubmission.class);
			for ( VerificationState state : submission.getStateHistory()) {
				VerificationSubmissionStateRecord stateRecord = new VerificationSubmissionStateRecord();
				stateRecord.setId(Long.parseLong(submission.getId()));
				stateRecord.setCreatedOn(state.getCreatedOn().getTime());
				stateRecord.setCreatedBy(Long.parseLong(state.getCreatedBy()));
				stateRecord.setState(state.getState());
				records.add(stateRecord);
			}
			return records;
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isValidVerificationSubmissionStateRecords(
			List<VerificationSubmissionStateRecord> records) {
		for (VerificationSubmissionStateRecord record : records) {
			if (!isValidVerificationSubmissionStateRecord(record)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidVerificationSubmissionStateRecord(
			VerificationSubmissionStateRecord record) {
		if (record.getId() 			== null) return false;
		if (record.getCreatedOn() 	== null) return false;
		if (record.getCreatedBy() 	== null) return false;
		if (record.getState() 		== null) return false;
		return true;
	}

}
