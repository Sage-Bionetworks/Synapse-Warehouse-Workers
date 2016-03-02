package org.sagebionetworks.warehouse.workers.db;

import org.sagebionetworks.warehouse.workers.model.CertifiedQuizRecord;

public interface CertifiedQuizRecordDao extends SnapshotDao<CertifiedQuizRecord> {

	/**
	 * 
	 * @return an CertifiedQuizRecord given the responseId
	 */
	public CertifiedQuizRecord get(Long responseId);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
