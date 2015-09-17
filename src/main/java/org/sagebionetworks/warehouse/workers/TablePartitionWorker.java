package org.sagebionetworks.warehouse.workers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	private static Logger log = LogManager.getLogger(TablePartitionWorker.class);
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
			if (!tableConfig.isCreateWithPartitions()) continue;
			progressCallback.progressMade(null);
			String tableName = tableConfig.getTableName();
			log.info("Checking partitions on table "+tableName+"...");
			Map<String, Long> requiredPartitions = PartitionUtil.getPartitions(tableName, tableConfig.getPartitionPeriod(), startDate, endDate);
			Set<String> existingPartitions = creator.getExistingPartitionsForTable(tableName);
			Set<String> toDrop = new HashSet<String>(existingPartitions);
			toDrop.removeAll(requiredPartitions.keySet());
			Set<String> toAdd = requiredPartitions.keySet();
			toAdd.removeAll(existingPartitions);
			for (String partitionName : toAdd) {
				log.info("Adding partition "+partitionName+"...");
				creator.addPartition(tableName, partitionName, requiredPartitions.get(partitionName));
			}
			for (String partitionName : toDrop) {
				if (partitionName != null) {
					log.info("Dropping partition "+partitionName+"...");
					creator.dropPartition(tableName, partitionName);
				}
			}
		}
	}

}
