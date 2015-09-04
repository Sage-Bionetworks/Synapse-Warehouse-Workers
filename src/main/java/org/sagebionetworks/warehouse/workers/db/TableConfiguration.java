package org.sagebionetworks.warehouse.workers.db;

import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;

/**
 * TableConfiguration holds table creation information including whether the
 * table should be created with partitions or not, and the partition's config.
 */
public class TableConfiguration {

	String tableName;
	String schemaFileName;
	boolean createWithPartitions;
	String partitionFieldName;
	Period partitionPeriod;
	public TableConfiguration(String tableName, String schemaFileName,
			boolean createWithPartitions, String partitionFieldName,
			Period period) {
		super();
		this.tableName = tableName;
		this.schemaFileName = schemaFileName;
		this.createWithPartitions = createWithPartitions;
		this.partitionFieldName = partitionFieldName;
		this.partitionPeriod = period;
	}
	public String getTableName() {
		return tableName;
	}
	public String getSchemaFileName() {
		return schemaFileName;
	}
	public boolean isCreateWithPartitions() {
		return createWithPartitions;
	}
	public String getPartitionFieldName() {
		return partitionFieldName;
	}
	public Period getPartitionPeriod() {
		return partitionPeriod;
	}
}
