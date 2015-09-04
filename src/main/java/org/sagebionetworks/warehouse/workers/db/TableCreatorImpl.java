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
	public void createTableWithPartition(String fileName, String tableName, String fieldName, Period period) {
		String query = ClasspathUtils.loadStringFromClassPath(fileName);
		if (!query.contains(PartitionUtil.PARTITION)) {
			throw new IllegalArgumentException();
		}
		String partitionString = PartitionUtil.buildPartition(tableName, fieldName, period, startDate, endDate);
		query = query.replace(PartitionUtil.PARTITION, partitionString);
		template.update(query);
	}

	@Override
	public void createTable(TableConfiguration config) {
		if (config.isCreateWithPartitions()) {
			createTableWithPartition(config.getSchemaFileName(),
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
}
