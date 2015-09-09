package org.sagebionetworks.warehouse.workers.db;

import java.util.List;

import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;

public interface ProcessedAccessRecordDao {

	/**
	 * Insert a batch of ProcessedAccessRecord into PROCESSED_ACCESS_RECORD table
	 * 
	 * @param batch
	 */
	public void insert(List<ProcessedAccessRecord> batch);

	/**
	 * 
	 * @return a ProcessedAccessRecord given the sessionId and timestamp
	 */
	public ProcessedAccessRecord get(String sessionId, Long timestamp);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();

	/**
	 * 
	 * @param timeMS
	 * @return true if there is a partition for this timeMS,
	 *         false otherwise.
	 */
	public boolean doesPartitionExistForTimestamp(long timeMS);
}
