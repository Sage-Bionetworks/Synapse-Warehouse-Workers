package org.sagebionetworks.warehouse.workers.db;

import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;

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
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSchemaFileName() {
		return schemaFileName;
	}
	public void setSchemaFileName(String schemaFileName) {
		this.schemaFileName = schemaFileName;
	}
	public boolean isCreateWithPartitions() {
		return createWithPartitions;
	}
	public void setCreateWithPartitions(boolean createWithPartitions) {
		this.createWithPartitions = createWithPartitions;
	}
	public String getPartitionFieldName() {
		return partitionFieldName;
	}
	public void setPartitionFieldName(String partitionFieldName) {
		this.partitionFieldName = partitionFieldName;
	}
	public Period getPartitionPeriod() {
		return partitionPeriod;
	}
	public void setPartitionPeriod(Period partitionPeriod) {
		this.partitionPeriod = partitionPeriod;
	}
}
