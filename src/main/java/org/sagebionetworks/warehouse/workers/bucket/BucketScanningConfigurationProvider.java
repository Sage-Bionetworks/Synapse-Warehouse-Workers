package org.sagebionetworks.warehouse.workers.bucket;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.warehouse.workers.SemaphoreKey;
import org.sagebionetworks.warehouse.workers.WorkerStackConfiguration;
import org.sagebionetworks.warehouse.workers.WorkerStackConfigurationProvider;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedWorkerStack;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedWorkerStackConfiguration;

import com.google.inject.Inject;

/**
 * This worker stack will scan all buckets to find files and folders that have
 * not been discovered by the real time process.  This worker's primary task is to back-fill
 * all S3 objects that existed before the stack start.
 * 
 */
public class BucketScanningConfigurationProvider implements WorkerStackConfigurationProvider {
	
	final WorkerStackConfiguration config;
	
	@Inject
	public BucketScanningConfigurationProvider(CountingSemaphore semaphore, BucketScanningWorker worker) {
		super();
		SemaphoreGatedWorkerStackConfiguration config = new SemaphoreGatedWorkerStackConfiguration();
		config.setProgressingRunner(worker);
		config.setSemaphoreLockKey(SemaphoreKey.BUCKET_SCANNING_WORKER.name());
		config.setSemaphoreLockTimeoutSec(30);
		config.setSemaphoreMaxLockCount(1);
		Runnable mainRunner = new SemaphoreGatedWorkerStack(semaphore, config);
		this.config = new WorkerStackConfiguration();
		this.config.setRunner(mainRunner);
		this.config.setStartDelayMs(2987);
		// run once per minute.
		this.config.setPeriodMS(60*1000);
		this.config.setWorkerName(BucketScanningWorker.class.getName());
	}

	@Override
	public WorkerStackConfiguration getWorkerConfiguration() {
		return this.config;
	}

}
