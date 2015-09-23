package org.sagebionetworks.warehouse.workers.bucket;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.warehouse.workers.db.FileManager;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.sqs.model.Message;
import com.google.inject.Inject;

/**
 * This worker response to S3 bucket events and forwards them to the file manager.
 * @author John
 *
 */
public class RealTimeBucketWorker implements MessageDrivenRunner {
	private static Logger log = LogManager.getLogger(RealTimeBucketWorker.class);
	private FileManager fileManager;

	@Inject
	public RealTimeBucketWorker(FileManager fileManager) {
		super();
		this.fileManager = fileManager;
	}


	@Override
	public void run(final ProgressCallback<Message> progressCallback, final Message message)
			throws RecoverableMessageException, Exception {
		log.info("Processing message... ");
		// let the manger know about the file
		List<S3ObjectSummary> messageDetails = EventMessageUtils.parseEventJson(message.getBody());
		// Notify the manger of this stream
		fileManager.addS3Objects(messageDetails.iterator(), new ProgressCallback<Void>() {
			@Override
			public void progressMade(Void arg0) {
				progressCallback.progressMade(message);
			}
		});
	}

}
