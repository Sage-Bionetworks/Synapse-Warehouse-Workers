package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.db.AccessRecordDao;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordUtils;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.model.Message;
import com.google.inject.Inject;

/**
 * This worker reader a collated access record file from S3 and write the data to ACCESS_RECORD table.
 */
public class AccessRecordWorker implements MessageDrivenRunner {

	private Logger log = LogManager.getLogger(AccessRecordWorker.class);
	private AmazonS3Client s3Client;
	private AccessRecordDao dao;

	@Inject
	public AccessRecordWorker(AmazonS3Client s3Client, AccessRecordDao dao) {
		super();
		this.s3Client = s3Client;
		this.dao = dao;
	}

	@Override
	public void run(ProgressCallback<Message> callback, Message message)
			throws RecoverableMessageException, Exception {
		ObjectCSVReader<AccessRecord> reader = AccessRecordWorkerUtil.getAccessRecordReader(message, s3Client);
		AccessRecord record = reader.next();
		List<AccessRecord> batch = new ArrayList<AccessRecord>();

		while (record != null) {
			if (!AccessRecordUtils.isValidAccessRecord(record)) {
				log.error("Invalid Access Record: " + record.toString());
				continue;
			}
			batch.add(record);
			if (batch.size() == 1000) {
				dao.insert(batch);
				batch = new ArrayList<AccessRecord>();
			}
			record = reader.next();
		}

		if (batch.size() > 0) {
			dao.insert(batch);
		}
	}

}
