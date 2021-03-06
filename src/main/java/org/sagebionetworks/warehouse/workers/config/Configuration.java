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
	 * Get the start date to backfill data
	 * 
	 * @return 
	 */
	public DateTime getBackfillStartDate();

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

	/**
	 * @return the day of the month that we will run the audit queries
	 */
	public int getMonthlyAuditDay();

	/**
	 * Get the start date to setup partitions
	 * 
	 * @return
	 */
	public DateTime getPartitionStartDate();

}
