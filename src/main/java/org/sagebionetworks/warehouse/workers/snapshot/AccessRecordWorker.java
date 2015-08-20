package org.sagebionetworks.warehouse.workers.snapshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.aws.utils.sns.MessageUtil;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.bucket.FileSubmissionMessage;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.AccessRecordDao;
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
 * This worker reader a collated access record file from S3 and write the data to ACCESS_RECORD table.
 */
public class AccessRecordWorker implements MessageDrivenRunner {

	private static final int BATCH_SIZE = 1000;
	private static Logger log = LogManager.getLogger(AccessRecordWorker.class);
	private AmazonS3Client s3Client;
	private AccessRecordDao dao;
	private StreamResourceProvider streamResourceProvider;

	@Inject
	public AccessRecordWorker(AmazonS3Client s3Client, AccessRecordDao dao,
			StreamResourceProvider streamResourceProvider) {
		super();
		this.s3Client = s3Client;
		this.dao = dao;
		this.streamResourceProvider = streamResourceProvider;
	}

	@Override
	public void run(ProgressCallback<Message> callback, Message message)
			throws RecoverableMessageException, IOException {
		callback.progressMade(message);

		// extract the bucket and key from the message
		String xml = MessageUtil.extractMessageBodyAsString(message);
		FileSubmissionMessage fileSubmissionMessage = XMLUtils.fromXML(xml, FileSubmissionMessage.class, "Message");

		// read the file as a stream
		File file = null;
		ObjectCSVReader<AccessRecord> reader = null;
		try {
			file = streamResourceProvider.createTempFile("collatedAccessRecords", ".csv.gz");
			s3Client.getObject(new GetObjectRequest(fileSubmissionMessage.getBucket(), fileSubmissionMessage.getKey()), file);
			reader = streamResourceProvider.createObjectCSVReader(file, AccessRecord.class);

			writeAccessRecord(reader, dao, BATCH_SIZE);

		} finally {
			if (reader != null) 	reader.close();
			if (file != null) 		file.delete();
		}
	}

	/**
	 * Read access records from reader, and write them to ACCESS_RECORD table using dao
	 * 
	 * @param reader
	 * @param dao
	 * @throws IOException
	 */
	public static void writeAccessRecord(ObjectCSVReader<AccessRecord> reader,
			AccessRecordDao dao, int batchSize) throws IOException {
		AccessRecord record = reader.next();
		List<AccessRecord> batch = new ArrayList<AccessRecord>();

		while (record != null) {
			if (!AccessRecordUtils.isValidAccessRecord(record)) {
				log.error("Invalid Access Record: " + record.toString());
				record = reader.next();
				continue;
			}
			batch.add(record);
			if (batch.size() == batchSize) {
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
