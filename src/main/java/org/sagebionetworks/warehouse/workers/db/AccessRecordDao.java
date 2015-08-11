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
	 * @return an AccessRecord given the sessionId
	 */
	public AccessRecord get(String sessionId);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
