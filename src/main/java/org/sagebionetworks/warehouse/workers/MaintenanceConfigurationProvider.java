package org.sagebionetworks.warehouse.workers;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedWorkerStack;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedWorkerStackConfiguration;

import com.google.inject.Inject;

public class MaintenanceConfigurationProvider implements WorkerStackConfigurationProvider{

	final WorkerStackConfiguration config;

	@Inject
	public MaintenanceConfigurationProvider(CountingSemaphore semaphore, MaintenanceWorker worker) {
		super();
		SemaphoreGatedWorkerStackConfiguration config = new SemaphoreGatedWorkerStackConfiguration();
		config.setProgressingRunner(worker);
		config.setSemaphoreLockKey(SemaphoreKey.MAINTENANCE_WORKER.name());
		config.setSemaphoreLockTimeoutSec(30);
		config.setSemaphoreMaxLockCount(1);
		Runnable mainRunner = new SemaphoreGatedWorkerStack(semaphore, config);
		this.config = new WorkerStackConfiguration();
		this.config.setRunner(mainRunner);
		this.config.setStartDelayMs(1013);
		// run once per 10 minutes.
		this.config.setPeriodMS(10*60*1000);
		this.config.setWorkerName(MaintenanceWorker.class.getName());
	}

	@Override
	public WorkerStackConfiguration getWorkerConfiguration() {
		return config;
	}

}
