package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.repo.model.audit.FileHandleSnapshot;

public interface FileHandleRecordDao extends SnapshotDao<FileHandleSnapshot> {

	/**
	 * 
	 * @return a FileHandleSnapshot given the fileHandleId
	 */
	public FileHandleSnapshot get(Long fileHandleId);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
