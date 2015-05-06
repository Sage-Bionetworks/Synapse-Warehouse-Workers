package org.sagebionetworks.warehouse.workers.db;

public interface FileMetadataDao {

	/**
	 * Get the state of a file tracked in the database.
	 * @param bucket
	 * @param key
	 * @return
	 */
	public FileState getFileState(String bucket, String key);
	
}
