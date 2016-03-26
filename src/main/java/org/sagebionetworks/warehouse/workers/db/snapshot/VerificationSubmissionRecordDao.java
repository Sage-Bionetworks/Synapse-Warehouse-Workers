package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionRecord;

public interface VerificationSubmissionRecordDao extends SnapshotDao<VerificationSubmissionRecord> {

	/**
	 * 
	 * @return an VerificationSubmissionRecord given the submission ID
	 */
	public VerificationSubmissionRecord get(Long id);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
