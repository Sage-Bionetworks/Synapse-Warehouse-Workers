package org.sagebionetworks.warehouse.workers.collate;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.warehouse.workers.SemaphoreKey;
import org.sagebionetworks.warehouse.workers.WorkerStackConfiguration;
import org.sagebionetworks.warehouse.workers.WorkerStackConfigurationProvider;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenWorkerStack;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenWorkerStackConfiguration;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.google.inject.Inject;

/**
 * Setup for a message driven worker stack that will process folder collation messages.
 *
 */
public class CollateFolderConfigurationProvider implements WorkerStackConfigurationProvider {
	
	WorkerStackConfiguration config;

	@Inject
	public CollateFolderConfigurationProvider(CountingSemaphore semaphore, AmazonSQSClient awsSQSClient, AmazonSNSClient awsSNSClient, CollateMessageQueue queue, FolderLockingWorker worker){
		MessageDrivenWorkerStackConfiguration messageConfig = new MessageDrivenWorkerStackConfiguration();
		messageConfig.setQueueName(queue.getMessageQueue().getQueueName());
		messageConfig.setRunner(worker);
		messageConfig.setSemaphoreLockAndMessageVisibilityTimeoutSec(60);
		messageConfig.setSemaphoreLockKey(SemaphoreKey.FOLDER_COLLATE_WORKER.name());
		messageConfig.setSemaphoreMaxLockCount(10);
		MessageDrivenWorkerStack stack = new MessageDrivenWorkerStack(semaphore, awsSQSClient, awsSNSClient, messageConfig);
		
		config = new WorkerStackConfiguration();
		config.setPeriodMS(1000*2);
		config.setRunner(stack);
		config.setStartDelayMs(1333);
		config.setWorkerName(FolderLockingWorker.class.getName());
	}
	
	@Override
	public WorkerStackConfiguration getWorkerConfiguration() {
		return config;
	}


}
