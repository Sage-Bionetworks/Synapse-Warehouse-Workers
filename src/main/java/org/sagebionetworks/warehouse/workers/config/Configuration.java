package org.sagebionetworks.warehouse.workers.config;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.warehouse.workers.WorkerStackConfigurationProvider;

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
	public List<Class<? extends WorkerStackConfigurationProvider>> listAllWorkerStackInterfaces();

	/**
	 * Get the date <current year - 2>-01-01:00:00
	 * 
	 * @return 
	 */
	public DateTime getStartDate();

	/**
	 * Get the date <current year + 9>-01-01:00:00
	 * 
	 * @return
	 */
	public DateTime getEndDate();
}
