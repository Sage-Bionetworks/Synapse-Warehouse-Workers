package org.sagebionetworks.warehouse.workers.bucket;

import org.sagebionetworks.warehouse.workers.db.FileManager;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.sqs.model.Message;
import com.google.inject.Inject;

public class RealTimeBucketWorker implements MessageDrivenRunner {
	
	private FileManager fileManager;
	
	@Inject
	public RealTimeBucketWorker(FileManager fileManager) {
		super();
		this.fileManager = fileManager;
	}


	@Override
	public void run(ProgressCallback<Message> progressCallback, Message message)
			throws RecoverableMessageException, Exception {
		
		// let the manger know about the file
		

	}

}
