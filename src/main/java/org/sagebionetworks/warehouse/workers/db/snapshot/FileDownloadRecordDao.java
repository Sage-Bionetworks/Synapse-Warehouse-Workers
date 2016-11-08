package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.warehouse.workers.model.FileDownload;

public interface FileDownloadRecordDao extends SnapshotDao<FileDownload> {

	/**
	 * 
	 * @return an FileDownload object
	 */
	public FileDownload get(Long timestamp, Long userId, Long fileHandleId, Long objectId, FileHandleAssociateType objectType);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
