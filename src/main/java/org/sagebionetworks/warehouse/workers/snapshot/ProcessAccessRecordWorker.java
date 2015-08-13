package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.ArrayList;
import java.util.List;

import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.db.ProcessedAccessRecordDao;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordUtils;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.model.Message;
import com.google.inject.Inject;

/**
 * This worker reader a collated access record file from S3, processes it,
 * and write the processed data to PROCESSED_ACCESS_RECORD table.
 */
public class ProcessAccessRecordWorker implements MessageDrivenRunner {

	private AmazonS3Client s3Client;
	private ProcessedAccessRecordDao dao;

	@Inject
	public ProcessAccessRecordWorker(AmazonS3Client s3Client, ProcessedAccessRecordDao dao) {
		super();
		this.s3Client = s3Client;
		this.dao = dao;
	}

	@Override
	public void run(ProgressCallback<Message> callback, Message message)
			throws RecoverableMessageException, Exception {
		ObjectCSVReader<AccessRecord> reader = AccessRecordWorkerUtil.getAccessRecordReader(message, s3Client);
		AccessRecord record = reader.next();
		List<ProcessedAccessRecord> batch = new ArrayList<ProcessedAccessRecord>();

		while (record != null) {
			batch.add(AccessRecordUtils.processAccessRecord(record));
			if (batch.size() == 1000) {
				dao.insert(batch);
				batch = new ArrayList<ProcessedAccessRecord>();
			}
			record = reader.next();
		}

		if (batch.size() > 0) {
			dao.insert(batch);
		}
	}

}
