package org.sagebionetworks.warehouse.workers.db;

import org.sagebionetworks.warehouse.workers.model.FileState;
import org.sagebionetworks.warehouse.workers.model.FileState.State;

public interface FileMetadataDao {

	/**
	 * Get the state of a file tracked in the database.
	 * 
	 * @param bucket
	 * @param key
	 * @return
	 */
	public FileState getFileState(String bucket, String key);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();

	/**
	 * Set the state of a file to FAILED.
	 * 
	 * @param bucket
	 *            File's bucket
	 * @param key
	 *            File's key
	 * @param reason
	 *            Details of the error.
	 */
	public void setFileStateFailed(String bucket, String key, Throwable reason);

	/**
	 * Set the date of a file to a given state.
	 * Note: This will clear any error message and details.
	 * @param bucket
	 * @param key
	 * @param state
	 */
	public void setFileState(String bucket, String key, State state);
	
	/**
	 * Does the given file exist?
	 * @param bucket
	 * @param key
	 * @return
	 */
	public boolean doesFileExist(String bucket, String key);

}
