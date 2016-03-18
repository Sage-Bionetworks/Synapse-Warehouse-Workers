package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.db.HasPartitions;

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
