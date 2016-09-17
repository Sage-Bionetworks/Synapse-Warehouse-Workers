package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.VerificationSubmissionRecordDao;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionRecord;
import org.sagebionetworks.warehouse.workers.utils.VerificationSubmissionSnapshotUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class VerificationSubmissionRecordWorker extends AbstractSnapshotWorker<ObjectRecord, VerificationSubmissionRecord> implements SnapshotWorker<ObjectRecord, VerificationSubmissionRecord> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedVerificationSubmissionRecord";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public VerificationSubmissionRecordWorker(AmazonS3Client s3Client, VerificationSubmissionRecordDao dao,
			StreamResourceProvider streamResourceProvider) {
		super(s3Client, dao, streamResourceProvider);
		log = LogManager.getLogger(VerificationSubmissionRecordWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<VerificationSubmissionRecord> convert(ObjectRecord record) {
		VerificationSubmissionRecord snapshot = VerificationSubmissionSnapshotUtils.getVerificationSubmissionRecord(record);
		if (!VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionRecord(snapshot)) {
			log.error("Invalid Verification Submission Record from: " + record.toString());
			return null;
		}
		return Arrays.asList(snapshot);
	}
}
