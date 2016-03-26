package org.sagebionetworks.warehouse.workers.utils;

import java.util.ArrayList;
import java.util.List;

import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.quiz.PassingRecord;
import org.sagebionetworks.repo.model.quiz.ResponseCorrectness;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.CertifiedQuizQuestionRecord;
import org.sagebionetworks.warehouse.workers.model.CertifiedQuizRecord;

public class PassingRecordSnapshotUtils {

	public static CertifiedQuizRecord getCertifiedQuizRecord(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(PassingRecord.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			PassingRecord passingRecord = EntityFactory.createEntityFromJSONString(record.getJsonString(), PassingRecord.class);
			CertifiedQuizRecord certifiedQuizRecord = new CertifiedQuizRecord();
			certifiedQuizRecord.setResponseId(passingRecord.getResponseId());
			certifiedQuizRecord.setUserId(Long.parseLong(passingRecord.getUserId()));
			certifiedQuizRecord.setPassed(passingRecord.getPassed());
			certifiedQuizRecord.setPassedOn(passingRecord.getPassedOn().getTime());
			return certifiedQuizRecord;
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isValidCertifiedQuizRecord(CertifiedQuizRecord record) {
		if (record.getResponseId() 		== null) return false;
		if (record.getUserId() 			== null) return false;
		if (record.getPassed() 			== null) return false;
		if (record.getPassedOn() 		== null) return false;
		return true;
	}

	public static List<CertifiedQuizQuestionRecord> getCertifiedQuizQuestionRecords(
			ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(PassingRecord.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			List<CertifiedQuizQuestionRecord> records = new ArrayList<CertifiedQuizQuestionRecord>();
			PassingRecord passingRecord = EntityFactory.createEntityFromJSONString(record.getJsonString(), PassingRecord.class);
			for ( ResponseCorrectness question : passingRecord.getCorrections()) {
				CertifiedQuizQuestionRecord questionRecord = new CertifiedQuizQuestionRecord();
				questionRecord.setResponseId(passingRecord.getResponseId());
				questionRecord.setQuestionIndex(question.getQuestion().getQuestionIndex());
				questionRecord.setIsCorrect(question.getIsCorrect());
				records.add(questionRecord);
			}
			return records;
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isValidCertifiedQuizQuestionRecords(
			List<CertifiedQuizQuestionRecord> records) {
		for (CertifiedQuizQuestionRecord record : records) {
			if (!isValidCertifiedQuizQuestionRecord(record)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidCertifiedQuizQuestionRecord(
			CertifiedQuizQuestionRecord record) {
		if (record.getResponseId() 		== null) return false;
		if (record.getQuestionIndex() 	== null) return false;
		if (record.getIsCorrect() 		== null) return false;
		return true;
	}

}
