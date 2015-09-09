package org.sagebionetworks.warehouse.workers.db;

import java.util.List;

import org.sagebionetworks.repo.model.audit.AccessRecord;

public interface AccessRecordDao {

	/**
	 * Insert a batch of AccessRecord into ACCESS_RECORD table
	 * 
	 * @param batch
	 */
	public void insert(List<AccessRecord> batch);

	/**
	 * 
	 * @return an AccessRecord given the sessionId and timestamp
	 */
	public AccessRecord get(String sessionId, Long timestamp);

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
