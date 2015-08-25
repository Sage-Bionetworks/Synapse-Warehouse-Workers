package org.sagebionetworks.warehouse.workers.collate;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.warehouse.workers.SemaphoreKey;
import org.sagebionetworks.warehouse.workers.WorkerStackConfiguration;
import org.sagebionetworks.warehouse.workers.WorkerStackConfigurationProvider;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedWorkerStack;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedWorkerStackConfiguration;

import com.google.inject.Inject;

/**
 * This stack runs as a singleton on a timer. The stack will find all folders
 * with a state of 'rolling' For each folder found a message will be pushed to
 * the collate worker queue.
 * 
 * @author John
 * 
 */
public class PeriodicRollingFolderConfigurationProvider implements
		WorkerStackConfigurationProvider {

	WorkerStackConfiguration config;
	
	@Inject
	public PeriodicRollingFolderConfigurationProvider(CountingSemaphore semaphore, PeriodicRollingFolderMessageGeneratorWorker worker){
		SemaphoreGatedWorkerStackConfiguration config = new SemaphoreGatedWorkerStackConfiguration();
		config.setProgressingRunner(worker);
		config.setSemaphoreLockKey(SemaphoreKey.PERIODIC_ROLLING_FOLDER_MESSAGE_GENERATOR.name());
		config.setSemaphoreLockTimeoutSec(30);
		// This is singleton.
		config.setSemaphoreMaxLockCount(1);
		Runnable mainRunner = new SemaphoreGatedWorkerStack(semaphore, config);
		this.config = new WorkerStackConfiguration();
		this.config.setRunner(mainRunner);
		this.config.setStartDelayMs(3);
		// This worker only runs every 20 mins.
		this.config.setPeriodMS(60*1000*20);
		this.config.setWorkerName(PeriodicRollingFolderMessageGeneratorWorker.class.getName());
	}

	@Override
	public WorkerStackConfiguration getWorkerConfiguration() {
		return config;
	}

}
