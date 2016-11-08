package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.Arrays;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.warehouse.workers.RunDuringNormalStateGate;
import org.sagebionetworks.warehouse.workers.SemaphoreKey;
import org.sagebionetworks.warehouse.workers.WorkerStackConfiguration;
import org.sagebionetworks.warehouse.workers.WorkerStackConfigurationProvider;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenWorkerStack;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenWorkerStackConfiguration;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.google.inject.Inject;

public class BulkFileHandleDownloadRecordConfigurationProvider implements WorkerStackConfigurationProvider {

	final WorkerStackConfiguration config;

	@Inject
	public BulkFileHandleDownloadRecordConfigurationProvider (CountingSemaphore semaphore,
			AmazonSQSClient awsSQSClient, AmazonSNSClient awsSNClient,
			BulkFileHandleDownloadRecordWorker worker, BulkFileHandleDownloadRecordTopicBucketInfo config,
			RunDuringNormalStateGate gate) {

		MessageDrivenWorkerStackConfiguration mdwsc = new MessageDrivenWorkerStackConfiguration();
		mdwsc.setGate(gate);
		mdwsc.setQueueName(config.getQueueName());
		mdwsc.setTopicNamesToSubscribe(Arrays.asList(config.getTopicName()));
		mdwsc.setRunner(worker);
		mdwsc.setSemaphoreLockAndMessageVisibilityTimeoutSec(60);
		mdwsc.setSemaphoreLockKey(SemaphoreKey.BULK_FILE_HANDLE_DOWNLOAD_RECORD_WORKER.name());
		mdwsc.setSemaphoreMaxLockCount(1);

		Runnable runner = new MessageDrivenWorkerStack(semaphore, awsSQSClient,
				awsSNClient, mdwsc);
		this.config = new WorkerStackConfiguration();
		this.config.setRunner(runner);
		this.config.setStartDelayMs(711);
		this.config.setPeriodMS(10*1000);
		this.config.setWorkerName(BulkFileHandleDownloadRecordWorker.class.getName());
	}

	@Override
	public WorkerStackConfiguration getWorkerConfiguration() {
		return this.config;
	}

}
