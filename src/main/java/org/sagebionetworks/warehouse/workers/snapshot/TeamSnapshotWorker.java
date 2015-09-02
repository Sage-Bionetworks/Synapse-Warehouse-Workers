package org.sagebionetworks.warehouse.workers.snapshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.aws.utils.sns.MessageUtil;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.warehouse.workers.bucket.FileSubmissionMessage;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.TeamSnapshotDao;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotUtils;
import org.sagebionetworks.warehouse.workers.utils.XMLUtils;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.sqs.model.Message;
import com.google.inject.Inject;

public class TeamSnapshotWorker implements MessageDrivenRunner {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedTeamSnapshot";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";
	private static final int BATCH_SIZE = 10000;
	private static Logger log = LogManager.getLogger(TeamSnapshotWorker.class);
	private AmazonS3Client s3Client;
	private TeamSnapshotDao dao;
	private StreamResourceProvider streamResourceProvider;

	@Inject
	public TeamSnapshotWorker(AmazonS3Client s3Client, TeamSnapshotDao dao,
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

		// read the file as a stream
		File file = null;
		ObjectCSVReader<ObjectRecord> reader = null;
		try {
			file = streamResourceProvider.createTempFile(TEMP_FILE_NAME_PREFIX, TEMP_FILE_NAME_SUFFIX);
			s3Client.getObject(new GetObjectRequest(fileSubmissionMessage.getBucket(), fileSubmissionMessage.getKey()), file);
			reader = streamResourceProvider.createObjectCSVReader(file, ObjectRecord.class, SnapshotHeader.OBJECT_RECORD_HEADERS);

			log.info("Processing " + fileSubmissionMessage.getBucket() + "/" + fileSubmissionMessage.getKey());
			long start = System.currentTimeMillis();
			int noRecords = writeTeamSnapshot(reader, dao, BATCH_SIZE, callback, message);
			log.info("Wrote " + noRecords + " records in " + (System.currentTimeMillis() - start) + " mili seconds");

		} finally {
			if (reader != null) 	reader.close();
			if (file != null) 		file.delete();
		}
	}

	/**
	 * Read team snapshot records from reader, and write them to TEAM_SNAPSHOT table using dao
	 * 
	 * @param reader
	 * @param dao
	 * @return number of records written
	 * @throws IOException
	 */
	public static int writeTeamSnapshot(ObjectCSVReader<ObjectRecord> reader,
			TeamSnapshotDao dao, int batchSize, ProgressCallback<Message> callback,
			Message message) throws IOException {
		ObjectRecord record = null;
		List<TeamSnapshot> batch = new ArrayList<TeamSnapshot>(batchSize);

		int noRecords = 0;
		while ((record = reader.next()) != null) {
			TeamSnapshot snapshot = ObjectSnapshotUtils.getTeamSnapshot(record);
			if (!ObjectSnapshotUtils.isValidTeamSnapshot(snapshot)) {
				log.error("Invalid Team Snapshot from Record: " + record.toString());
				continue;
			}
			batch.add(snapshot);
			if (batch.size() >= batchSize) {
				callback.progressMade(message);
				dao.insert(batch);
				noRecords += batch.size();
				batch .clear();
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
