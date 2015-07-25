package org.sagebionetworks.warehouse.workers.bucket;

import org.sagebionetworks.workers.util.progress.ProgressCallback;

public interface FolderCollateWorker {

	/**
	 * Collate a folder.
	 * Note: This method should only be called while holding a lock on folder.
	 * 
	 * @param progressCallback
	 * @param folder
	 */
	void run(ProgressCallback<Void> progressCallback, FolderDto folder);

}
