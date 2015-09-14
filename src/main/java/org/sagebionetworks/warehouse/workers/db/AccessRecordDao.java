package org.sagebionetworks.warehouse.workers.db;

import org.sagebionetworks.repo.model.audit.AccessRecord;

public interface AccessRecordDao extends HasPartitions, SnapshotDao<AccessRecord> {

	/**
	 * 
	 * @return an AccessRecord given the sessionId and timestamp
	 */
	public AccessRecord get(String sessionId, Long timestamp);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();

}
