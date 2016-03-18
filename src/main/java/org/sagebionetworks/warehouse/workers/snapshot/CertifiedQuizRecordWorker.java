package org.sagebionetworks.warehouse.workers.snapshot;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.aws.utils.sns.MessageUtil;
import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.csv.utils.ObjectCSVReader;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.bucket.FileSubmissionMessage;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.CertifiedQuizRecordDao;
import org.sagebionetworks.warehouse.workers.model.CertifiedQuizRecord;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.PassingRecordSnapshotUtils;
import org.sagebionetworks.warehouse.workers.utils.XMLUtils;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.sqs.model.Message;
import com.google.inject.Inject;

public class CertifiedQuizRecordWorker implements MessageDrivenRunner, SnapshotWorker<ObjectRecord, CertifiedQuizRecord> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedCertifiedQuizRecord";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";
	private static final int BATCH_SIZE = 25000;
	private static Logger log = LogManager.getLogger(CertifiedQuizRecordWorker.class);
	private AmazonS3Client s3Client;
	private CertifiedQuizRecordDao dao;
	private StreamResourceProvider streamResourceProvider;

	@Inject
	public CertifiedQuizRecordWorker(AmazonS3Client s3Client, CertifiedQuizRecordDao dao,
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

		// read the file as a stream
		File file = null;
		ObjectCSVReader<ObjectRecord> reader = null;
		try {
			file = streamResourceProvider.createTempFile(TEMP_FILE_NAME_PREFIX, TEMP_FILE_NAME_SUFFIX);
			log.info("Downloading file: "+ fileSubmissionMessage.getBucket() + "/" + fileSubmissionMessage.getKey());
			s3Client.getObject(new GetObjectRequest(fileSubmissionMessage.getBucket(), fileSubmissionMessage.getKey()), file);
			log.info("Download completed.");
			reader = streamResourceProvider.createObjectCSVReader(file, ObjectRecord.class, SnapshotHeader.OBJECT_RECORD_HEADERS);

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
	public List<CertifiedQuizRecord> convert(ObjectRecord record) {
		CertifiedQuizRecord snapshot = PassingRecordSnapshotUtils.getCertifiedQuizRecord(record);
		if (!PassingRecordSnapshotUtils.isValidCertifiedQuizRecord(snapshot)) {
			log.error("Invalid Certified Quiz Record from: " + record.toString());
			return null;
		}
		return Arrays.asList(snapshot);
	}
}
