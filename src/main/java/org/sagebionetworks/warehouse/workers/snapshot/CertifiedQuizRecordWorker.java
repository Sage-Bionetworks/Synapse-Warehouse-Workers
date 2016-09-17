package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.CertifiedQuizRecordDao;
import org.sagebionetworks.warehouse.workers.model.CertifiedQuizRecord;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.PassingRecordSnapshotUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class CertifiedQuizRecordWorker extends AbstractSnapshotWorker<ObjectRecord, CertifiedQuizRecord> implements  SnapshotWorker<ObjectRecord, CertifiedQuizRecord> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedCertifiedQuizRecord";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";

	@Inject
	public CertifiedQuizRecordWorker(AmazonS3Client s3Client, CertifiedQuizRecordDao dao,
			StreamResourceProvider streamResourceProvider) {
		super(s3Client, dao, streamResourceProvider);
		log = LogManager.getLogger(CertifiedQuizRecordWorker.class);
		tempFileNamePrefix = TEMP_FILE_NAME_PREFIX;
		tempFileNameSuffix = TEMP_FILE_NAME_SUFFIX;
		snapshotHeader = SnapshotHeader.OBJECT_RECORD_HEADERS;
		clazz = ObjectRecord.class;
	}

	@Override
	public List<CertifiedQuizRecord> convert(ObjectRecord record) {
		CertifiedQuizRecord snapshot = PassingRecordSnapshotUtils.getCertifiedQuizRecord(record);
		if (!PassingRecordSnapshotUtils.isValidCertifiedQuizRecord(snapshot)) {
			log.error("Invalid Certified Quiz Record from: " + record.toString());
			return null;
		}
		return Arrays.asList(snapshot);
	}
}
