package org.sagebionetworks.warehouse.workers.db;

import java.util.List;


public interface FolderMetadataDao {
	
	/**
	 * A rolling folder is one that contains rolling files.  Call this method to mark a folder as rolling.
	 * @param bucketName The name of the folder's bucket.
	 * @param key The full path of the folder.
	 * @param modifiedOnTimeMS The modified on of the file that triggered this call.
	 */
	public void markFolderAsRolling(String bucketName, String path, long modifiedOnTimeMS);
	
	/**
	 * A rolling folder is one that contains rolling files. This will list the full path of 
	 * each rolling folder for a given bucket ordered by the modifiedOn descending.
	 * 
	 * @param bucketName
	 * @return
	 */
	public List<FolderState> listRollingFolders(String bucketName);
	
	/**
	 * Clear all data from this table.
	 */
	public void truncateTable();
	

}
