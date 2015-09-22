package org.sagebionetworks.warehouse.workers.snapshot;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.aws.utils.s3.KeyData;
import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;
import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.aws.utils.sns.MessageUtil;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.bucket.FileSubmissionMessage;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.AccessRecordDao;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
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
public class AccessRecordWorker implements MessageDrivenRunner, SnapshotWorker<AccessRecord, AccessRecord> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedAccessRecords";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";
	private static final int BATCH_SIZE = 25000;
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
		FileSubmissionMessage fileSubmissionMessage = XMLUtils.fromXML(xml, FileSubmissionMessage.class, FileSubmissionMessage.ALIAS);

		log.info("Received message for key: "+ fileSubmissionMessage.getBucket() + "/" + fileSubmissionMessage.getKey());

		KeyData keyData = KeyGeneratorUtil.parseKey(fileSubmissionMessage.getKey());
		if (!dao.doesPartitionExistForTimestamp(keyData.getTimeMS())) {
			log.info("Missing partition for timestamp: "+keyData.getTimeMS()+". Putting message back...");
			throw new RecoverableMessageException();
		}

		// read the file as a stream
		File file = null;
		ObjectCSVReader<AccessRecord> reader = null;
		try {
			file = streamResourceProvider.createTempFile(TEMP_FILE_NAME_PREFIX, TEMP_FILE_NAME_SUFFIX);
			log.info("Downloading file: "+ fileSubmissionMessage.getBucket() + "/" + fileSubmissionMessage.getKey());
			s3Client.getObject(new GetObjectRequest(fileSubmissionMessage.getBucket(), fileSubmissionMessage.getKey()), file);
			log.info("Download completed.");
			reader = streamResourceProvider.createObjectCSVReader(file, AccessRecord.class, SnapshotHeader.ACCESS_RECORD_HEADERS);

			log.info("Processing " + fileSubmissionMessage.getBucket() + "/" + fileSubmissionMessage.getKey());
			long start = System.currentTimeMillis();
			int noRecords = SnapshotWriter.write(reader, dao, BATCH_SIZE, callback, message, this);
			log.info("Inserted (ignore) " + noRecords + " records in " + (System.currentTimeMillis() - start) + " mili seconds");

		} catch (Exception e) {
			log.info(e.toString());
		} finally {
			if (reader != null) 	reader.close();
			if (file != null) 		file.delete();
		}
	}

	@Override
	public AccessRecord convert(AccessRecord record) {
		if (!AccessRecordUtils.isValidAccessRecord(record)) {
			log.error("Invalid Access Record: " + record.toString());
			return null;
		}
		return record;
	}

}
