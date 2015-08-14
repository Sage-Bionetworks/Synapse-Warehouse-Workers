package org.sagebionetworks.warehouse.workers.snapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.aws.utils.sns.MessageUtil;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.bucket.FileSubmissionMessage;
import org.sagebionetworks.warehouse.workers.db.ProcessedAccessRecordDao;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordUtils;
import org.sagebionetworks.warehouse.workers.utils.XMLUtils;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.sqs.model.Message;
import com.google.inject.Inject;

/**
 * This worker reader a collated access record file from S3, processes it,
 * and write the processed data to PROCESSED_ACCESS_RECORD table.
 */
public class ProcessAccessRecordWorker implements MessageDrivenRunner {

	private static final int BATCH_SIZE = 1000;
	private static Logger log = LogManager.getLogger(ProcessAccessRecordWorker.class);
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
			throws RecoverableMessageException, IOException {
		// extract the bucket and key from the message
		String xml = MessageUtil.extractMessageBodyAsString(message);
		FileSubmissionMessage fileSubmissionMessage = XMLUtils.fromXML(xml, FileSubmissionMessage.class, "Message");

		// read the file as a stream
		File file = null;
		try {
			File.createTempFile("collatedAccessRecords", ".csv.gz");
			s3Client.getObject(new GetObjectRequest(fileSubmissionMessage.getBucket(), fileSubmissionMessage.getKey()), file);
			ObjectCSVReader<AccessRecord> reader = new ObjectCSVReader<AccessRecord>(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "UTF-8"), AccessRecord.class);

			writeProcessedAcessRecord(reader, dao);

			reader.close();
		} finally {
			if (file != null)
				file.delete();
		}
	}

	/**
	 * Read access records from reader, process them, and write the processed
	 * access records to PROCESSED_ACCESS_RECORD table using dao
	 * 
	 * @param reader
	 * @param dao
	 * @throws IOException
	 */
	public static void writeProcessedAcessRecord(ObjectCSVReader<AccessRecord> reader, ProcessedAccessRecordDao dao)
			throws IOException {
		AccessRecord record = reader.next();
		List<ProcessedAccessRecord> batch = new ArrayList<ProcessedAccessRecord>();

		while (record != null) {
			if (!AccessRecordUtils.isValidAccessRecord(record)) {
				log.error("Invalid Access Record: " + record.toString());
				continue;
			}
			batch.add(AccessRecordUtils.processAccessRecord(record));
			if (batch.size() == BATCH_SIZE) {
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
