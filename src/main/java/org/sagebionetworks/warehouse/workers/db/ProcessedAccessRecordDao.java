package org.sagebionetworks.warehouse.workers.db;

import java.util.List;

import org.sagebionetworks.warehouse.workers.utils.ProcessedAccessRecord;

public interface ProcessedAccessRecordDao {

	/**
	 * Insert a batch of ProcessedAccessRecord into PROCESSED_ACCESS_RECORD table
	 * 
	 * @param batch
	 */
	public void insert(List<ProcessedAccessRecord> batch);

	/**
	 * 
	 * @return all ProcessedAccessRecord that has CLIENT set to UNKNOWN
	 */
	public List<ProcessedAccessRecord> getUnknownClient();

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
