package org.sagebionetworks.warehouse.workers.collate;

import org.sagebionetworks.workers.util.aws.message.MessageQueue;

/**
 * Wraps the collate message queue.
 * 
 * @author John
 *
 */
public class CollateMessageQueue {
	
	MessageQueue messageQueue;

	public CollateMessageQueue(MessageQueue mockMessageQueue) {
		this.messageQueue = mockMessageQueue;
	}

	public MessageQueue getMessageQueue() {
		return messageQueue;
	}

	public void setMessageQueue(MessageQueue messageQueue) {
		this.messageQueue = messageQueue;
	}
	
}
