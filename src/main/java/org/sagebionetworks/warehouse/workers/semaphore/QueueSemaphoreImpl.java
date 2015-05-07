package org.sagebionetworks.warehouse.workers.semaphore;

import org.sagebionetworks.warehouse.workers.config.Configuration;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.google.inject.Inject;

public class QueueSemaphoreImpl {
	
	AmazonSQSClient sqsClient;
	Configuration config;
	
	@Inject
	public QueueSemaphoreImpl(Configuration config) {
		this.config = config;
		this.sqsClient = config.createSQSClient();
	}
	
	
	

}
