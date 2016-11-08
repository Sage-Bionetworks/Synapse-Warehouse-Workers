package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.warehouse.workers.model.FileHandleDownload;

public interface FileHandleDownloadRecordDao extends SnapshotDao<FileHandleDownload> {

	/**
	 * 
	 * @return an FileHandleDownload object
	 */
	public FileHandleDownload get(Long timestamp, Long userId, Long downloadedFileHandleId, Long requestedfileHandleId, Long objectId, FileHandleAssociateType objectType);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
