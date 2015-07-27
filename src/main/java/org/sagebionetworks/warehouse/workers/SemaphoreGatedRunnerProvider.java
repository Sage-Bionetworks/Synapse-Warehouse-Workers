package org.sagebionetworks.warehouse.workers;

import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedRunner;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedRunnerConfiguration;

/**
 * Abstraction for creating new SemaphoreGatedRunners from configuration data.
 *
 */
public interface SemaphoreGatedRunnerProvider {

	/**
	 * Create a new SemaphoreGatedRunner from the configuration.
	 * @param config
	 * @return
	 */
	<T> SemaphoreGatedRunner createRunner(SemaphoreGatedRunnerConfiguration<T> config);
}
