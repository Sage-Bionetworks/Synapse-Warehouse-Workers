package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.CertifiedQuizQuestionRecordDao;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.warehouse.workers.model.CertifiedQuizQuestionRecord;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.PassingRecordSnapshotUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class CertifiedQuizQuestionRecordWorker extends AbstractSnapshotWorker<ObjectRecord, CertifiedQuizQuestionRecord> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedCertifiedQuizQuestionRecord";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public CertifiedQuizQuestionRecordWorker(AmazonS3Client s3Client, CertifiedQuizQuestionRecordDao dao,
			StreamResourceProvider streamResourceProvider, AmazonLogger amazonLogger) {
		super(s3Client, dao, streamResourceProvider, amazonLogger);
		log = LogManager.getLogger(CertifiedQuizQuestionRecordWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<CertifiedQuizQuestionRecord> convert(ObjectRecord record) {
		List<CertifiedQuizQuestionRecord> records = PassingRecordSnapshotUtils.getCertifiedQuizQuestionRecords(record);
		if (!PassingRecordSnapshotUtils.isValidCertifiedQuizQuestionRecords(records)) {
			throw new IllegalArgumentException("Invalid Certified Quiz Question Record from: "+ (record == null ? "null" : record.toString()));
		}
		return records;
	}
}
