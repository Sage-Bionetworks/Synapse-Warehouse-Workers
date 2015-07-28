package org.sagebionetworks.warehouse.workers;

import java.util.LinkedList;
import java.util.List;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfo;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfoList;
import org.sagebionetworks.warehouse.workers.bucket.BucketScanningStack;
import org.sagebionetworks.warehouse.workers.bucket.BucketTopicPublisher;
import org.sagebionetworks.warehouse.workers.bucket.BucketTopicPublisherImpl;
import org.sagebionetworks.warehouse.workers.bucket.FolderCollateWorker;
import org.sagebionetworks.warehouse.workers.bucket.FolderLockingWorker;
import org.sagebionetworks.warehouse.workers.bucket.LockedFolderRunner;
import org.sagebionetworks.warehouse.workers.bucket.RealTimeBucketListenerStack;
import org.sagebionetworks.warehouse.workers.bucket.RealtimeBucketListenerStackConfig;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.FileManager;
import org.sagebionetworks.warehouse.workers.db.FileManagerImpl;

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
		bind(BucketTopicPublisher.class).to(BucketTopicPublisherImpl.class);
		bind(FileManager.class).to(FileManagerImpl.class);
		bind(LockedFolderRunner.class).to(FolderCollateWorker.class);
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
	public RealtimeBucketListenerStackConfig getBucketListenerConfig(Configuration config){
		RealtimeBucketListenerStackConfig rtbls = new RealtimeBucketListenerStackConfig();
		rtbls.setTopicName(config.getProperty("org.sagebionetworks.warehouse.worker.topic.all.bucket.events"));
		rtbls.setQueueName(config.getProperty("org.sagebionetworks.warehouse.worker.queue.all.bucket.events"));
		return rtbls;
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
}
