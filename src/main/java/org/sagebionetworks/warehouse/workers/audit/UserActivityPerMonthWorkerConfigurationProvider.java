package org.sagebionetworks.warehouse.workers.audit;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.warehouse.workers.SemaphoreKey;
import org.sagebionetworks.warehouse.workers.WorkerStackConfiguration;
import org.sagebionetworks.warehouse.workers.WorkerStackConfigurationProvider;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedWorkerStack;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedWorkerStackConfiguration;

import com.google.inject.Inject;

public class UserActivityPerMonthWorkerConfigurationProvider implements WorkerStackConfigurationProvider{

	final WorkerStackConfiguration config;

	@Inject
	public UserActivityPerMonthWorkerConfigurationProvider(CountingSemaphore semaphore, UserActivityPerMonthWorker worker) {
		super();
		SemaphoreGatedWorkerStackConfiguration config = new SemaphoreGatedWorkerStackConfiguration();
		config.setProgressingRunner(worker);
		config.setSemaphoreLockKey(SemaphoreKey.USER_ACTIVITY_PER_MONTH_WORKER.name());
		config.setSemaphoreLockTimeoutSec(30);
		config.setSemaphoreMaxLockCount(1);
		Runnable mainRunner = new SemaphoreGatedWorkerStack(semaphore, config);
		this.config = new WorkerStackConfiguration();
		this.config.setRunner(mainRunner);
		this.config.setStartDelayMs(1013);
		// run once per 4 hour.
		this.config.setPeriodMS(4*60*60*1000);
		this.config.setWorkerName(UserActivityPerMonthWorker.class.getName());
	}

	@Override
	public WorkerStackConfiguration getWorkerConfiguration() {
		return config;
	}

}
