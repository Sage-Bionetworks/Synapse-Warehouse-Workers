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

public class TeamMemberSnapshotConfigurationProvider implements WorkerStackConfigurationProvider {

	final WorkerStackConfiguration config;

	@Inject
	public TeamMemberSnapshotConfigurationProvider (CountingSemaphore semaphore,
			AmazonSQSClient awsSQSClient, AmazonSNSClient awsSNClient,
			TeamMemberSnapshotWorker worker, TeamMemberSnapshotTopicBucketInfo config) {

		MessageDrivenWorkerStackConfiguration mdwsc = new MessageDrivenWorkerStackConfiguration();
		mdwsc.setQueueName(config.getQueueName());
		mdwsc.setTopicNamesToSubscribe(Arrays.asList(config.getTopicName()));
		mdwsc.setRunner(worker);
		mdwsc.setSemaphoreLockAndMessageVisibilityTimeoutSec(60);
		mdwsc.setSemaphoreLockKey(SemaphoreKey.TEAM_MEMBER_SNAPSHOT_WORKER.name());
		mdwsc.setSemaphoreMaxLockCount(1);

		Runnable runner = new MessageDrivenWorkerStack(semaphore, awsSQSClient,
				awsSNClient, mdwsc);
		this.config = new WorkerStackConfiguration();
		this.config.setRunner(runner);
		this.config.setStartDelayMs(307);
		this.config.setPeriodMS(10*1000);
		this.config.setWorkerName(TeamMemberSnapshotWorker.class.getName());
	}

	@Override
	public WorkerStackConfiguration getWorkerConfiguration() {
		return this.config;
	}

}
