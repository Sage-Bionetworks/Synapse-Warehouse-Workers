package org.sagebionetworks.warehouse.workers.db;

import java.util.Set;

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
	public void createTableWithPartitions(String fileName, String tableName, String fieldName, Period period);

	/**
	 * Create table with one partition
	 * 
	 * @param fileName
	 * @param tableName
	 * @param fieldName
	 * @param period
	 */
	public void createTableWithDefaultPartition(String fileName, String tableName, String fieldName, Period period);

	/**
	 * Create a table based on the config
	 * 
	 * @param config
	 */
	public void createTable(TableConfiguration config);

	/**
	 * Create a table using the query defined in fileName
	 * If the query contains PARTITION, ignore it
	 * 
	 * @param fileName
	 */
	public void createTableWithoutPartitions(String fileName);

	/**
	 * 
	 * @param tableName
	 * @param partitionName
	 * @return true is tableName has partition partitionName,
	 *         false otherwise.
	 */
	public boolean doesPartitionExist(String tableName, String partitionName);

	/**
	 * Alter table tableName, add a partition partitionName by range with values
	 * less than value
	 * 
	 * @param tableName
	 * @param partitionName
	 * @param value
	 */
	public void addPartition(String tableName, String partitionName, long value);

	/**
	 * 
	 * @param tableName
	 * @return a set of existing partitions for the given table
	 */
	public Set<String> getExistingPartitionsForTable(String tableName);

	/**
	 * Drop old partition partitionName from table tableName
	 * 
	 * @param tableName
	 * @param partitionName
	 */
	public void dropPartition(String tableName, String partitionName);

	/**
	 * 
	 * @param tableName
	 * @param timeMS
	 * @param partitionPeriod
	 * @return true if partitions exist for timeMS on table tableName given partitionPeriod
	 */
	public boolean doesPartitionExist(String tableName, long timeMS, Period partitionPeriod);
}
