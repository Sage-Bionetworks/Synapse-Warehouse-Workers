package org.sagebionetworks.warehouse.workers;

import java.util.LinkedList;
import java.util.List;

import org.sagebionetworks.warehouse.workers.bucket.BucketInfo;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfoList;
import org.sagebionetworks.warehouse.workers.bucket.BucketTopicPublisher;
import org.sagebionetworks.warehouse.workers.bucket.BucketTopicPublisherImpl;
import org.sagebionetworks.warehouse.workers.bucket.RealtimeBucketListenerStackConfig;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.FileManager;
import org.sagebionetworks.warehouse.workers.db.FileManagerImpl;

import com.google.inject.AbstractModule;
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
				.withTimestampColumnIndex(4));
		// acl snapshot
		list.add(new BucketInfo()
				.withBucketName(
						config.getProperty("org.sagebionetworks.warehouse.worker.bucket.snapshot.acl"))
				.withTimestampColumnIndex(0));
		// s3 copy snapshot
		list.add(new BucketInfo()
				.withBucketName(
						config.getProperty("org.sagebionetworks.warehouse.worker.bucket.snapshot.s3filecopyresults"))
				.withTimestampColumnIndex(0));
		// team snapshot
		list.add(new BucketInfo()
		.withBucketName(
				config.getProperty("org.sagebionetworks.warehouse.worker.bucket.snapshot.team"))
		.withTimestampColumnIndex(0));
		// team member snapshot
		list.add(new BucketInfo()
		.withBucketName(
				config.getProperty("org.sagebionetworks.warehouse.worker.bucket.snapshot.teammember"))
		.withTimestampColumnIndex(0));
		// user profile snapshot
		list.add(new BucketInfo()
		.withBucketName(
				config.getProperty("org.sagebionetworks.warehouse.worker.bucket.snapshot.userprofile"))
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
	
}
