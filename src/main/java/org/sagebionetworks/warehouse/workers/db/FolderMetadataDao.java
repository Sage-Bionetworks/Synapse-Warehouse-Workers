package org.sagebionetworks.warehouse.workers.db;

import java.util.Iterator;

public interface FolderMetadataDao {

	/**
	 * Create the folder state if it does not exist. If state already exists for
	 * the folder it will be update.
	 * 
	 * @param bucketName
	 *            The name of the folder's bucket.
	 * @param key
	 *            The full path of the folder.
	 * @param modifiedOnTimeMS
	 *            The modified on of the file that triggered this call.
	 */
	public void createOrUpdateFolderState(FolderState state);

	/**
	 * List all folders with the given state from the given bucket.
	 * 
	 * @param bucketName
	 * @return
	 */
	public Iterator<FolderState> listFolders(String bucketName, FolderState.State state);

	/**
	 * Clear all data from this table.
	 */
	public void truncateTable();

}
