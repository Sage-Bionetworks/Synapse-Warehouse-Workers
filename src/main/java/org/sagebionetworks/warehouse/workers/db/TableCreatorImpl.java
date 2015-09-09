package org.sagebionetworks.warehouse.workers.db;

import org.joda.time.DateTime;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.utils.ClasspathUtils;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.inject.Inject;

public class TableCreatorImpl implements TableCreator {
	private JdbcTemplate template;
	private DateTime startDate;
	private DateTime endDate;
	public static final String CHECK_PARTITION = "SELECT COUNT(*) "
			+ "FROM INFORMATION_SCHEMA.PARTITIONS "
			+ "WHERE TABLE_NAME = ? AND PARTITION_NAME = ?";
	public static final String ADD_PARTITION = "ALTER TABLE ? "
			+ "ADD PARTITION (PARTITION ? VALUES LESS THAN (?))";

	@Inject
	TableCreatorImpl (JdbcTemplate template, Configuration config) {
		this.template = template;
		startDate = config.getStartDate();
		endDate = config.getEndDate();
	}

	@Override
	public void createTable(String fileName) {
		template.update(ClasspathUtils.loadStringFromClassPath(fileName));
	}

	@Override
	public void createTableWithPartitions(String fileName, String tableName, String fieldName, Period period) {
		if (fileName == null ||
				tableName == null ||
				fieldName == null ||
				period == null ||
				tableName.contains(PartitionUtil.PARTITION))
			throw new IllegalArgumentException();
		String query = ClasspathUtils.loadStringFromClassPath(fileName);
		if (!query.contains(PartitionUtil.PARTITION)) {
			throw new IllegalArgumentException();
		}
		String partitionString = PartitionUtil.buildPartitions(tableName, fieldName, period, startDate, endDate);
		query = query.replace(PartitionUtil.PARTITION, partitionString);
		template.update(query);
	}

	@Override
	public void createTable(TableConfiguration config) {
		if (config.isCreateWithPartitions()) {
			createTableWithPartitions(config.getSchemaFileName(),
					config.getTableName(), config.getPartitionFieldName(),
					config.getPartitionPeriod());
		} else {
			createTable(config.getSchemaFileName());
		}
	}

	@Override
	public void createTableWithoutPartitions(String fileName) {
		String query = ClasspathUtils.loadStringFromClassPath(fileName);
		if (query.contains(PartitionUtil.PARTITION)) {
			query = query.replace(PartitionUtil.PARTITION, "");
		}
		template.update(query);
	}

	@Override
	public boolean doesPartitionExist(String tableName, String partitionName) {
		return template.queryForLong(CHECK_PARTITION, tableName, partitionName) == 1;
	}

	@Override
	public void addPartition(String tableName, String partitionName, long value) {
		template.update(ADD_PARTITION, tableName, partitionName, value);
	}
}
