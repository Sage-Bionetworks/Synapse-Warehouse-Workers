package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.warehouse.workers.model.CertifiedQuizQuestionRecord;

public interface CertifiedQuizQuestionRecordDao extends SnapshotDao<CertifiedQuizQuestionRecord> {

	/**
	 * 
	 * @return an CertifiedQuizQuestionRecord given the responseId and questionIndex
	 */
	public CertifiedQuizQuestionRecord get(Long responseId, Long questionIndex);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
