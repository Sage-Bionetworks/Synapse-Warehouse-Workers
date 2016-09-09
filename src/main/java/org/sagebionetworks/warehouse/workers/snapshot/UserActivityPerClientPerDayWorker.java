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
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.bucket.FileSubmissionMessage;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.UserActivityPerClientPerDayDao;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerClientPerDay;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordUtils;
import org.sagebionetworks.warehouse.workers.utils.XMLUtils;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.sqs.model.Message;
import com.google.inject.Inject;

/**
 * This worker reader a collated access record file from S3, extract user info
 * from it, and write the user data to USER_ACCESS_RECORD table.
 */
public class UserActivityPerClientPerDayWorker implements MessageDrivenRunner, SnapshotWorker<AccessRecord, UserActivityPerClientPerDay> {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedAccessRecordsToProcess";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";
	private static final int BATCH_SIZE = 25000;
	private static Logger log = LogManager.getLogger(UserActivityPerClientPerDayWorker.class);
	private AmazonS3Client s3Client;
	private UserActivityPerClientPerDayDao dao;
	private StreamResourceProvider streamResourceProvider;

	@Inject
	public UserActivityPerClientPerDayWorker(AmazonS3Client s3Client, UserActivityPerClientPerDayDao dao,
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
	public List<UserActivityPerClientPerDay> convert(AccessRecord record) {
		if (!AccessRecordUtils.isValidAccessRecord(record)) {
			log.error("Invalid Access Record: " + record.toString());
			return null;
		}
		return Arrays.asList(AccessRecordUtils.getUserActivityPerClientPerDay(record));
	}

}
