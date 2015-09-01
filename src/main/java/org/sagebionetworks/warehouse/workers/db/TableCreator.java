package org.sagebionetworks.warehouse.workers.db;

import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;

public interface TableCreator {

	/**
	 * Create a table using the query defined in fileName
	 * 
	 * @param fileName
	 */
	public void createTable(String fileName);

	/**
	 * Create a table using the query defined in fileName and add partitions
	 * 
	 * @param fileName
	 * @param tableName
	 * @param fieldName - the field that is used for partitioning the table
	 * @param period - the period that defines the range of a partition
	 */
	public void createTableWithPartition(String fileName, String tableName, String fieldName, Period period);
}
