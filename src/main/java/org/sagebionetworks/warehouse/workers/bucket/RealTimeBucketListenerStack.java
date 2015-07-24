package org.sagebionetworks.warehouse.workers.bucket;

import java.util.Arrays;

import org.sagebionetworks.aws.utils.s3.BucketListener;
import org.sagebionetworks.aws.utils.s3.BucketListenerConfiguration;
import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.warehouse.workers.WorkerStackConfiguration;
import org.sagebionetworks.warehouse.workers.WorkerStackConfigurationProvider;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenWorkerStack;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenWorkerStackConfiguration;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Event;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.google.inject.Inject;

/**
 * This stack listens to the real-time events of each configured bucket.
 * 
 */
public class RealTimeBucketListenerStack implements WorkerStackConfigurationProvider {

	final WorkerStackConfiguration config;

	@Inject
	public RealTimeBucketListenerStack(CountingSemaphore semaphore,
			AmazonSQSClient awsSQSClient, AmazonS3Client awsS3Client,
			AmazonSNSClient awsSNClient, BucketInfoList bucketList,
			RealtimeBucketListenerStackConfig config,
			RealTimeBucketWorker worker) {

		// ensure a listener is setup for each bucket
		BucketListenerConfiguration listenerConfig = new BucketListenerConfiguration();
		listenerConfig.setConfigName("realtimeBucketListenerConfig");
		listenerConfig.setEvent(S3Event.ObjectCreated);
		listenerConfig.setTopicName(config.getTopicName());
		for (BucketInfo info : bucketList.getBucketList()) {
			listenerConfig.setBucketName(info.getBucketName());
			BucketListener listener = new BucketListener(awsS3Client,
					awsSNClient, listenerConfig);
		}
		// Build the stack to
		MessageDrivenWorkerStackConfiguration mdwsc = new MessageDrivenWorkerStackConfiguration();
		mdwsc.setQueueName(config.getQueueName());
		mdwsc.setTopicNamesToSubscribe(Arrays.asList(config.getTopicName()));
		mdwsc.setRunner(worker);
		mdwsc.setSemaphoreLockAndMessageVisibilityTimeoutSec(60);
		mdwsc.setSemaphoreLockKey("bucketListenerWorker");
		mdwsc.setSemaphoreMaxLockCount(4);
		Runnable mainRunner = new MessageDrivenWorkerStack(semaphore, awsSQSClient,
				awsSNClient, mdwsc);
		
		this.config = new WorkerStackConfiguration();
		this.config.setRunner(mainRunner);
		this.config.setStartDelayMs(987);
		this.config.setPeriodMS(10*1000);
	}

	@Override
	public WorkerStackConfiguration getWorkerConfiguration() {
		return this.config;
	}

}
