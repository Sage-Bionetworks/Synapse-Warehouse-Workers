package org.sagebionetworks.warehouse.workers.bucket;

import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.sqs.model.Message;

public class RealTimeBucketWorker implements MessageDrivenRunner {


	@Override
	public void run(ProgressCallback<Message> progressCallback, Message message)
			throws RecoverableMessageException, Exception {
		
		System.out.println(message.getBody());

	}

}
