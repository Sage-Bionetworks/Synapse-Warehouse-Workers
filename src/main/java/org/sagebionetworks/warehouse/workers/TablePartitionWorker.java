package org.sagebionetworks.warehouse.workers;

import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.db.TableConfigurationList;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil;
import org.sagebionetworks.workers.util.progress.ProgressCallback;
import org.sagebionetworks.workers.util.progress.ProgressingRunner;

import com.google.inject.Inject;

public class TablePartitionWorker implements ProgressingRunner<Void> {

	private TableConfigurationList tableConfigList;
	private TableCreator creator;
	private Configuration config;

	@Inject
	public TablePartitionWorker (TableConfigurationList list, TableCreator creator, Configuration config) {
		super();
		this.tableConfigList = list;
		this.creator = creator;
		this.config = config;
	}

	@Override
	public void run(ProgressCallback<Void> progressCallback) throws Exception {
		DateTime startDate = config.getStartDate();
		DateTime endDate = config.getEndDate();
		for (TableConfiguration tableConfig : tableConfigList.getList()) {
			String tableName = tableConfig.getTableName();
			Map<String, Long> requiredPartitions = PartitionUtil.getPartitionsForPeriod(tableName, tableConfig.getPartitionPeriod(), startDate, endDate);
			Set<String> existingPartitions = creator.getExistingPartitionsForTable(tableName);
			Set<String> toDrop = existingPartitions;
			toDrop.removeAll(requiredPartitions.keySet());
			Set<String> toAdd = requiredPartitions.keySet();
			toAdd.removeAll(existingPartitions);
			for (String partitionName : toAdd) {
				creator.addPartition(tableName, partitionName, requiredPartitions.get(partitionName));
			}
			for (String partitionName : toDrop) {
				creator.dropPartition(tableName, partitionName);
			}
		}
	}

}
