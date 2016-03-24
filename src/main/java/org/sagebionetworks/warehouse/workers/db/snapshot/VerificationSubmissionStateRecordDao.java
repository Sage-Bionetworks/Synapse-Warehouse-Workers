package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.repo.model.verification.VerificationStateEnum;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionStateRecord;

public interface VerificationSubmissionStateRecordDao extends SnapshotDao<VerificationSubmissionStateRecord> {

	/**
	 * 
	 * @return an VerificationSubmissionStateRecord given the submission ID and state
	 */
	public VerificationSubmissionStateRecord get(Long id, VerificationStateEnum state);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
