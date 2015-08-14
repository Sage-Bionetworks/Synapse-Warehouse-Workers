package org.sagebionetworks.warehouse.workers;

import java.util.LinkedList;
import java.util.List;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfo;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfoList;
import org.sagebionetworks.warehouse.workers.bucket.BucketScanningStack;
import org.sagebionetworks.warehouse.workers.bucket.BucketTopicPublisher;
import org.sagebionetworks.warehouse.workers.bucket.BucketTopicPublisherImpl;
import org.sagebionetworks.warehouse.workers.bucket.RealTimeBucketListenerStack;
import org.sagebionetworks.warehouse.workers.bucket.RealtimeBucketListenerTopicBucketInfo;
import org.sagebionetworks.warehouse.workers.bucket.TopicDaoProvider;
import org.sagebionetworks.warehouse.workers.bucket.TopicDaoProviderImpl;
import org.sagebionetworks.warehouse.workers.collate.CollateFolderStack;
import org.sagebionetworks.warehouse.workers.collate.CollateMessageQueue;
import org.sagebionetworks.warehouse.workers.collate.CollateProvider;
import org.sagebionetworks.warehouse.workers.collate.CollateProviderImpl;
import org.sagebionetworks.warehouse.workers.collate.FolderCollateWorker;
import org.sagebionetworks.warehouse.workers.collate.LockedFolderRunner;
import org.sagebionetworks.warehouse.workers.collate.PeriodicRollingFolderStack;
import org.sagebionetworks.warehouse.workers.collate.S3ObjectCollator;
import org.sagebionetworks.warehouse.workers.collate.S3ObjectCollatorImpl;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.FileManager;
import org.sagebionetworks.warehouse.workers.db.FileManagerImpl;
import org.sagebionetworks.warehouse.workers.snapshot.AccessRecordTopicBucketInfo;
import org.sagebionetworks.warehouse.workers.snapshot.ProcessAccessRecordTopicBucketInfo;
import org.sagebionetworks.workers.util.aws.message.MessageQueueConfiguration;
import org.sagebionetworks.workers.util.aws.message.MessageQueueImpl;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;


/**
 * Bindings for workers.
 * 
 */
public class WorkersModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(BucketDaoProvider.class).to(BucketDaoProviderImpl.class);
		bind(TopicDaoProvider.class).to(TopicDaoProviderImpl.class);
		bind(BucketTopicPublisher.class).to(BucketTopicPublisherImpl.class);
		bind(FileManager.class).to(FileManagerImpl.class);
		bind(LockedFolderRunner.class).to(FolderCollateWorker.class);
		bind(CollateProvider.class).to(CollateProviderImpl.class);
		bind(S3ObjectCollator.class).to(S3ObjectCollatorImpl.class);
	}

	/**
	 * Configuration for all buckets that are to be collated.
	 * 
	 * @param config
	 * @return
	 */
	@Provides
	public BucketInfoList getBucketsToCollate(Configuration config) {
		List<BucketInfo> list = new LinkedList<BucketInfo>();
		// access record
		list.add(new BucketInfo()
				.withBucketName(
						config.getProperty("org.sagebionetworks.warehouse.worker.bucket.access.record"))
				.withTimestampColumnIndex(2));
		// object snapshots
		list.add(new BucketInfo()
				.withBucketName(
						config.getProperty("org.sagebionetworks.warehouse.worker.bucket.snapshot.record"))
				.withTimestampColumnIndex(0));
		return new BucketInfoList(list);
	}
	
	@Provides
	public RealtimeBucketListenerTopicBucketInfo getBucketListenerConfig(Configuration config){
		RealtimeBucketListenerTopicBucketInfo rtbls = new RealtimeBucketListenerTopicBucketInfo();
		rtbls.setTopicName(config.getProperty("org.sagebionetworks.warehouse.worker.topic.all.bucket.events"));
		rtbls.setQueueName(config.getProperty("org.sagebionetworks.warehouse.worker.queue.all.bucket.events"));
		return rtbls;
	}

	@Provides
	public AccessRecordTopicBucketInfo getAccessRecordConfig(Configuration config){
		AccessRecordTopicBucketInfo info = new AccessRecordTopicBucketInfo();
		info.setTopicName(config.getProperty("org.sagebionetworks.warehouse.worker.topic.accessrecord.snapshot"));
		info.setQueueName(config.getProperty("org.sagebionetworks.warehouse.worker.queue.accessrecord.snapshot"));
		return info;
	}

	@Provides
	public ProcessAccessRecordTopicBucketInfo getProcessedAccessRecordConfig(Configuration config){
		ProcessAccessRecordTopicBucketInfo info = new ProcessAccessRecordTopicBucketInfo();
		info.setTopicName(config.getProperty("org.sagebionetworks.warehouse.worker.topic.processaccessrecord.snapshot"));
		info.setQueueName(config.getProperty("org.sagebionetworks.warehouse.worker.queue.processaccessrecord.snapshot"));
		return info;
	}

	/**
	 * This the binding for all workers stacks. To add a new worker stack to the the application
	 * its class must be added to this list.
	 * 
	 * @return
	 */
	@Provides
	public WorkerStackConfigurationProviderList getWorkerStackConfigurationProviderList(){
		WorkerStackConfigurationProviderList list = new WorkerStackConfigurationProviderList();
		list.add(RealTimeBucketListenerStack.class);
		list.add(BucketScanningStack.class);
		list.add(PeriodicRollingFolderStack.class);
		list.add(CollateFolderStack.class);
		return list;
	}
	
	@Provides
	public WorkerStackList buildWorkerStackList(Injector injetor, WorkerStackConfigurationProviderList providerList){
		WorkerStackList list = new WorkerStackList();
		// create each worker stack from the providers
		for(Class<? extends WorkerStackConfigurationProvider> providerClass: providerList.getList()){
			WorkerStackConfigurationProvider provider = injetor.getInstance(providerClass);
			list.add(new WorkerStackImpl(provider.getWorkerConfiguration()));
		}
		return list;
	}
	
	@Provides
	public SemaphoreGatedRunnerProvider createSemaphoreGatedRunnerProvider(CountingSemaphore semaphore){
		return new SemaphoreGatedRunnerProviderImpl(semaphore);
	}
	
	@Provides
	public CollateMessageQueue createCollateMessageQueue(AmazonSQSClient awsSQSClient, AmazonSNSClient awsSNSClient, Configuration config){
		MessageQueueConfiguration messageConfig = new MessageQueueConfiguration();
		messageConfig.setQueueName(config.getProperty("org.sagebionetworks.warehouse.worker.collate.worker.queue.name"));
		messageConfig.setDefaultMessageVisibilityTimeoutSec(60);
		MessageQueueImpl queue = new MessageQueueImpl(awsSQSClient, awsSNSClient, messageConfig);
		return new CollateMessageQueue(queue);
	}
}

