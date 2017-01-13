package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.warehouse.workers.model.FileHandleCopyRecordSnapshot;

public interface FileHandleCopyRecordDao extends SnapshotDao<FileHandleCopyRecordSnapshot> {

	/**
	 * 
	 * @return an FileHandleCopyRecordSnapshot object
	 */
	public FileHandleCopyRecordSnapshot get(Long timestamp, Long userId, Long originalFileHandleId, Long objectId, FileHandleAssociateType objectType);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
