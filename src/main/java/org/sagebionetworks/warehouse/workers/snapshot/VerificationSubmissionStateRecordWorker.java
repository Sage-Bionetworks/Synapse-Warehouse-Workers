package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.VerificationSubmissionStateRecordDao;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionStateRecord;
import org.sagebionetworks.warehouse.workers.utils.VerificationSubmissionSnapshotUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class VerificationSubmissionStateRecordWorker extends AbstractSnapshotWorker<ObjectRecord, VerificationSubmissionStateRecord> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedVerificationSubmissionStateRecord";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public VerificationSubmissionStateRecordWorker(AmazonS3Client s3Client, VerificationSubmissionStateRecordDao dao,
			StreamResourceProvider streamResourceProvider) {
		super(s3Client, dao, streamResourceProvider);
		log = LogManager.getLogger(VerificationSubmissionStateRecordWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<VerificationSubmissionStateRecord> convert(ObjectRecord record) {
		List<VerificationSubmissionStateRecord> snapshots = VerificationSubmissionSnapshotUtils.getVerificationSubmissionStateRecords(record);
		if (!VerificationSubmissionSnapshotUtils.isValidVerificationSubmissionStateRecords(snapshots)) {
			log.error("Invalid Verification Submission Record from: "+ (record == null ? "null" : record.toString()));
			return null;
		}
		return snapshots;
	}
}
