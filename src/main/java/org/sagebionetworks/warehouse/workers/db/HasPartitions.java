package org.sagebionetworks.warehouse.workers.db;

/**
 * Dao for table that has partitions must implement this interface
 */
public interface HasPartitions {

	/**
	 * 
	 * @param timeMS
	 * @return true if there is a partition for this timeMS,
	 *         false otherwise.
	 */
	public boolean doesPartitionExistForTimestamp(long timeMS);
}
