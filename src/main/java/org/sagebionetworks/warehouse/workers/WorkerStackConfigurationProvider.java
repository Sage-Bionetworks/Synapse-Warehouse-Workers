package org.sagebionetworks.warehouse.workers;

public interface WorkerStackConfigurationProvider {

	/**
	 * Configuration information used to driver a worker stack.
	 * 
	 * @return
	 */
	public WorkerStackConfiguration getWorkerConfiguration();
}
