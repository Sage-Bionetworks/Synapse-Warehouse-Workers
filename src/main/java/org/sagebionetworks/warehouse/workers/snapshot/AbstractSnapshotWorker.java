package org.sagebionetworks.warehouse.workers.snapshot;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.sagebionetworks.aws.utils.s3.KeyData;
import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;
import org.sagebionetworks.aws.utils.sns.MessageUtil;
import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.csv.utils.ObjectCSVReader;
import org.sagebionetworks.warehouse.workers.bucket.FileSubmissionMessage;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.HasPartitions;
import org.sagebionetworks.warehouse.workers.db.snapshot.SnapshotDao;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.warehouse.workers.utils.XMLUtils;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.sqs.model.Message;

public abstract class AbstractSnapshotWorker<K,V> implements MessageDrivenRunner, SnapshotWorker<K,V> {
	private static final int BATCH_SIZE = 25000;
	private SnapshotDao<V> dao;
	private StreamResourceProvider streamResourceProvider;
	private AmazonS3Client s3Client;
	protected AmazonLogger amazonLogger;
	protected Logger log;
	protected String tempFileNamePrefix;
	protected String tempFileNameSuffix;
	protected String[] snapshotHeader;
	protected Class<K> clazz;

	public AbstractSnapshotWorker(AmazonS3Client s3Client, SnapshotDao<V> dao,
			StreamResourceProvider streamResourceProvider, AmazonLogger amazonLogger) {
		this.s3Client = s3Client;
		this.dao = dao;
		this.streamResourceProvider = streamResourceProvider;
		this.amazonLogger = amazonLogger;
	}

	@Override
	public void run(ProgressCallback<Message> callback, Message message)
			throws RecoverableMessageException, Exception {
		callback.progressMade(message);

		// extract the bucket and key from the message
		String xml = MessageUtil.extractMessageBodyAsString(message);
		FileSubmissionMessage fileSubmissionMessage = XMLUtils.fromXML(xml, FileSubmissionMessage.class, FileSubmissionMessage.ALIAS);

		log.info("Received message for key: "+ fileSubmissionMessage.getBucket() + "/" + fileSubmissionMessage.getKey());

		KeyData keyData = KeyGeneratorUtil.parseKey(fileSubmissionMessage.getKey());
		if (dao instanceof HasPartitions) {
			HasPartitions hasPartitionsDao = (HasPartitions) dao;
			if (!hasPartitionsDao.doesPartitionExistForTimestamp(keyData.getTimeMS())) {
				log.info("Missing partition for timestamp: "+keyData.getTimeMS()+". Putting message back...");
				throw new RecoverableMessageException();
			}
		}

		// read the file as a stream
		File file = null;
		ObjectCSVReader<K> reader = null;
		try {
			file = streamResourceProvider.createTempFile(tempFileNamePrefix, tempFileNameSuffix);
			log.info("Downloading file: "+ fileSubmissionMessage.getBucket() + "/" + fileSubmissionMessage.getKey());
			s3Client.getObject(new GetObjectRequest(fileSubmissionMessage.getBucket(), fileSubmissionMessage.getKey()), file);
			log.info("Download completed.");
			reader = streamResourceProvider.createObjectCSVReader(file, clazz, snapshotHeader);

			log.info("Processing " + fileSubmissionMessage.getBucket() + "/" + fileSubmissionMessage.getKey());
			long start = System.currentTimeMillis();
			int noRecords = SnapshotWriter.write(reader, dao, BATCH_SIZE, callback, message, this, amazonLogger);
			log.info("Inserted (ignore) " + noRecords + " records in " + (System.currentTimeMillis() - start) + " mili seconds");

		} catch (Exception e) {
			log.info(e.toString());
		} finally {
			if (reader != null) 	reader.close();
			if (file != null) 		file.delete();
		}
	}
}
