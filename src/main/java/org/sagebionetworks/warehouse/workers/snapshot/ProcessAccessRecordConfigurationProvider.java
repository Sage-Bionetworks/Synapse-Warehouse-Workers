package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.warehouse.workers.SemaphoreKey;
import org.sagebionetworks.warehouse.workers.WorkerStackConfiguration;
import org.sagebionetworks.warehouse.workers.WorkerStackConfigurationProvider;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenWorkerStack;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenWorkerStackConfiguration;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.google.inject.Inject;

public class ProcessAccessRecordConfigurationProvider implements WorkerStackConfigurationProvider {

	final WorkerStackConfiguration config;

	@Inject
	public ProcessAccessRecordConfigurationProvider (CountingSemaphore semaphore,
			AmazonSQSClient awsSQSClient, AmazonSNSClient awsSNClient,
			ProcessAccessRecordWorker worker, ProcessAccessRecordTopicBucketInfo config) {

		MessageDrivenWorkerStackConfiguration mdwsc = new MessageDrivenWorkerStackConfiguration();
		mdwsc.setQueueName(config.getQueueName());
		mdwsc.setTopicNamesToSubscribe(Arrays.asList(config.getTopicName()));
		mdwsc.setRunner(worker);
		mdwsc.setSemaphoreLockAndMessageVisibilityTimeoutSec(10*60);
		mdwsc.setSemaphoreLockKey(SemaphoreKey.PROCESS_ACCESS_RECORD_WORKER.name());
		mdwsc.setSemaphoreMaxLockCount(4);

		Runnable runner = new MessageDrivenWorkerStack(semaphore, awsSQSClient,
				awsSNClient, mdwsc);
		this.config = new WorkerStackConfiguration();
		this.config.setRunner(runner);
		this.config.setStartDelayMs(317);
		this.config.setPeriodMS(10*1000);
		this.config.setWorkerName(ProcessAccessRecordWorker.class.getName());
	}

	@Override
	public WorkerStackConfiguration getWorkerConfiguration() {
		return this.config;
	}

}
