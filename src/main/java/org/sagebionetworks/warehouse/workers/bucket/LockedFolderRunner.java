package org.sagebionetworks.warehouse.workers.bucket;

import org.sagebionetworks.workers.util.progress.ProgressCallback;

/**
 * An abstraction for a worker that runs while holding the lock on a folder's path.
 *
 */
public interface LockedFolderRunner {

	/**
	 * Do work while holding a lock on a folder's path.
	 * 
	 * @param progressCallback
	 * @param folder
	 */
	void runWhileHoldingLock(ProgressCallback<Void> progressCallback, FolderDto folder);

}
