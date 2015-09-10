package org.sagebionetworks.warehouse.workers.db;

import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;

public interface ProcessedAccessRecordDao extends HasPartitions, SnapshotDao<ProcessedAccessRecord>{

	/**
	 * 
	 * @return a ProcessedAccessRecord given the sessionId and timestamp
	 */
	public ProcessedAccessRecord get(String sessionId, Long timestamp);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
