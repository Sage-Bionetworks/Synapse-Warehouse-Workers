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
import org.sagebionetworks.warehouse.workers.db.NodeSnapshotDao;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotUtils;
import org.sagebionetworks.warehouse.workers.utils.XMLUtils;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.sqs.model.Message;
import com.google.inject.Inject;

public class NodeSnapshotWorker implements MessageDrivenRunner {

	public static final String TEMP_FILE_NAME_PREFIX = "collatedNodeSnapshot";
	public static final String TEMP_FILE_NAME_SUFFIX = ".csv.gz";
	private static final int BATCH_SIZE = 1000;
	private static Logger log = LogManager.getLogger(NodeSnapshotWorker.class);
	private AmazonS3Client s3Client;
	private NodeSnapshotDao dao;
	private StreamResourceProvider streamResourceProvider;

	@Inject
	public NodeSnapshotWorker(AmazonS3Client s3Client, NodeSnapshotDao dao,
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
			reader = streamResourceProvider.createObjectCSVReader(file, ObjectRecord.class);

			writeNodeSnapshot(reader, dao, BATCH_SIZE);

		} finally {
			if (reader != null) 	reader.close();
			if (file != null) 		file.delete();
		}
	}

	/**
	 * Read node snapshot records from reader, and write them to NODE_SNAPSHOT table using dao
	 * 
	 * @param reader
	 * @param dao
	 * @throws IOException
	 */
	public static void writeNodeSnapshot(ObjectCSVReader<ObjectRecord> reader,
			NodeSnapshotDao dao, int batchSize) throws IOException {
		ObjectRecord record = null;
		List<NodeSnapshot> batch = new ArrayList<NodeSnapshot>(batchSize);

		while ((record = reader.next()) != null) {
			NodeSnapshot snapshot = ObjectSnapshotUtils.getNodeSnapshot(record);
			if (!ObjectSnapshotUtils.isValidNodeSnapshot(snapshot)) {
				log.error("Invalid Node Snapshot: " + snapshot.toString() + " from Record: " + record.toString());
				continue;
			}
			batch.add(snapshot);
			if (batch.size() >= batchSize) {
				dao.insert(batch);
				batch .clear();
			}
		}

		if (batch.size() > 0) {
			dao.insert(batch);
		}
	}
}
