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
	 * Get the date two years ago
	 * 
	 * @return 
	 */
	public DateTime getStartDate();

	/**
	 * Get the date a week from now
	 * 
	 * @return
	 */
	public DateTime getEndDate();

	/**
	 * Get the time of the day we start the maintenance process
	 * 
	 * @return
	 */
	public int getMaintenanceStartTime();

	/**
	 * Get the time of the day we end the maintenance process
	 * 
	 * @return
	 */
	public int getMaintenanceEndTime();
}
