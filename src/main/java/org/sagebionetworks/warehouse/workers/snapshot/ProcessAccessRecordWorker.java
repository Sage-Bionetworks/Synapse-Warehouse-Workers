package org.sagebionetworks.warehouse.workers.snapshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.aws.utils.s3.KeyData;
import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;
import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.aws.utils.sns.MessageUtil;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.bucket.FileSubmissionMessage;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.ProcessedAccessRecordDao;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;
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
 * This worker reader a collated access record file from S3, processes it,
 * and write the processed data to PROCESSED_ACCESS_RECORD table.
 */
public class ProcessAccessRecordWorker implements MessageDrivenRunner {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedAccessRecords";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";
	private static final int BATCH_SIZE = 25000;
	private static Logger log = LogManager.getLogger(ProcessAccessRecordWorker.class);
	private AmazonS3Client s3Client;
	private ProcessedAccessRecordDao dao;
	private StreamResourceProvider streamResourceProvider;

	@Inject
	public ProcessAccessRecordWorker(AmazonS3Client s3Client, ProcessedAccessRecordDao dao,
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

		KeyData keyData = KeyGeneratorUtil.parseKey(fileSubmissionMessage.getKey());
		if (!dao.doesPartitionExistForTimestamp(keyData.getTimeMS())) {
			throw new RecoverableMessageException();
		}

		// read the file as a stream
		File file = null;
		ObjectCSVReader<AccessRecord> reader = null;
		try {
			file = streamResourceProvider.createTempFile(TEMP_FILE_NAME_PREFIX, TEMP_FILE_NAME_SUFFIX);
			s3Client.getObject(new GetObjectRequest(fileSubmissionMessage.getBucket(), fileSubmissionMessage.getKey()), file);
			reader = streamResourceProvider.createObjectCSVReader(file, AccessRecord.class, SnapshotHeader.ACCESS_RECORD_HEADERS);

			log.info("Processing " + fileSubmissionMessage.getBucket() + "/" + fileSubmissionMessage.getKey());
			long start = System.currentTimeMillis();
			int noRecords = writeProcessedAcessRecord(reader, dao, BATCH_SIZE, callback, message);
			log.info("Wrote " + noRecords + " records in " + (System.currentTimeMillis() - start) + " mili seconds");

		} finally {
			if (reader != null) 	reader.close();
			if (file != null) 		file.delete();
		}
	}

	/**
	 * Read access records from reader, process them, and write the processed
	 * access records to PROCESSED_ACCESS_RECORD table using dao
	 * 
	 * @param reader
	 * @param dao
	 * @param batchSize
	 * @return number of records written
	 * @throws IOException
	 */
	public static int writeProcessedAcessRecord(ObjectCSVReader<AccessRecord> reader,
			ProcessedAccessRecordDao dao, int batchSize, ProgressCallback<Message> callback,
			Message message) throws IOException {
		AccessRecord record = null;
		List<ProcessedAccessRecord> batch = new ArrayList<ProcessedAccessRecord>(batchSize);

		int noRecords = 0;
		while ((record = reader.next()) != null) {
			if (!AccessRecordUtils.isValidAccessRecord(record)) {
				log.error("Invalid Access Record: " + record.toString());
				continue;
			}
			batch.add(AccessRecordUtils.processAccessRecord(record));
			if (batch.size() >= batchSize) {
				callback.progressMade(message);
				dao.insert(batch);
				noRecords += batch.size();
				batch.clear();
			}
		}

		if (batch.size() > 0) {
			callback.progressMade(message);
			dao.insert(batch);
			noRecords += batch.size();
		}
		return noRecords;
	}

}
