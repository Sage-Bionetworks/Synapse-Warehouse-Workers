package org.sagebionetworks.warehouse.workers.db;

import java.util.Date;

public interface FolderMetadataDao {
	
	/**
	 * A rolling folder is one that contains rolling files.  Call this method to mark a folder as rolling.
	 * @param bucketName The name of the folder's bucket.
	 * @param key The name of the folder.
	 * @param modifiedOn The modified on of the file that triggered this call.
	 */
	public void markFolderAsRolling(String bucketName, String key, Date modifiedOn);

}
