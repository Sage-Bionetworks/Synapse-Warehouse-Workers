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
	 * @return a ProcessedAccessRecord given the sessionId
	 */
	public ProcessedAccessRecord get(String sessionId);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
