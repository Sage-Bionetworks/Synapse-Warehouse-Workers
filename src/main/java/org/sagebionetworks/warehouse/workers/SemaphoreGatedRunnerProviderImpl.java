package org.sagebionetworks.warehouse.workers;

import javax.inject.Inject;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedRunner;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedRunnerConfiguration;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedRunnerImpl;

public class SemaphoreGatedRunnerProviderImpl implements
		SemaphoreGatedRunnerProvider {
	
	private CountingSemaphore semaphore;

	@Inject
	public SemaphoreGatedRunnerProviderImpl(CountingSemaphore semaphore) {
		super();
		this.semaphore = semaphore;
	}

	@Override
	public <T> SemaphoreGatedRunner createRunner(
			SemaphoreGatedRunnerConfiguration<T> config) {
		return new SemaphoreGatedRunnerImpl<T>(semaphore, config);
	}

}
