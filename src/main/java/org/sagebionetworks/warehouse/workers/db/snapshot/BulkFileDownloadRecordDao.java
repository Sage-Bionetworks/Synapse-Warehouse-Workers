package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.warehouse.workers.model.BulkFileDownloadRecord;

public interface BulkFileDownloadRecordDao extends SnapshotDao<BulkFileDownloadRecord> {

	/**
	 * 
	 * @return an BulkFileDownloadRecord given the userId, objectId, and objectType
	 */
	public BulkFileDownloadRecord get(Long userId, Long objectId, FileHandleAssociateType objectType);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
