package org.sagebionetworks.warehouse.workers.config;

import java.util.List;

import org.sagebionetworks.warehouse.workers.WorkerStack;

/**
 * Abstraction for all of the configuration properties.
 *
 */
public interface Configuration {
		
	/**
	 * Get a configuration property by its key.
	 * @param key
	 * @return
	 */
	public String getProperty(String key);
	
	/**
	 * List all of the workers in this stack.
	 * @return
	 */
	public List<Class<? extends WorkerStack>> listAllWorkerStackInterfaces();
}
