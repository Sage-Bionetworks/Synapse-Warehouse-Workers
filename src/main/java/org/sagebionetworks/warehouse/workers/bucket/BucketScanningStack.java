package org.sagebionetworks.warehouse.workers.bucket;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.warehouse.workers.WorkerStack;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedWorkerStack;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedWorkerStackConfiguration;

import com.google.inject.Inject;

/**
 * This worker stack will scan all buckets to find files and folders that have
 * not been discovered by the real time process.  This worker's primary task is to back-fill
 * all S3 objects that existed before the stack start.
 * 
 */
public class BucketScanningStack implements WorkerStack {
	
	ScheduledExecutorService scheduler;
	Runnable stackRunner;
	
	@Inject
	public BucketScanningStack(CountingSemaphore semaphore, BucketScanningWorker worker) {
		super();
		int threadCount = 1;
		this.scheduler = Executors.newScheduledThreadPool(threadCount);
		SemaphoreGatedWorkerStackConfiguration config = new SemaphoreGatedWorkerStackConfiguration();
		config.setProgressingRunner(worker);
		config.setSemaphoreLockKey("bucketScanningWorker");
		config.setSemaphoreLockTimeoutSec(30);
		config.setSemaphoreMaxLockCount(1);
		this.stackRunner = new SemaphoreGatedWorkerStack(semaphore, config);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.WorkerStack#start()
	 */
	@Override
	public void start() {
		int startDalayMS = 987;
		int periodMS = 1013;
		// Start the worker.		
		scheduler.scheduleAtFixedRate(stackRunner, startDalayMS, periodMS, TimeUnit.MILLISECONDS);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.WorkerStack#shutdown()
	 */
	@Override
	public void shutdown() {
		scheduler.shutdown();
	}


}
