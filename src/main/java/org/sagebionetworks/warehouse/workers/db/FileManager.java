package org.sagebionetworks.warehouse.workers.db;

/**
 * Abstraction for all business logic around files and folder.
 *
 */
public interface FileManager {

	/**
	 * This method should be called for each key discovered either from a bucket
	 * scan or a bucket event. This method is idempotent so it can be called multiple
	 * times with the same key.
	 * 
	 * When called with a rolling file, the folder containing the file will be marked
	 * as "rolling".
	 * 
	 * For non-rolling files, if the file has already been processed then this call will do nothing.
	 * If the file has not been processed before, then a message will be sent to the bucket's topic
	 * for processing.
	 * 
	 * 
	 * @param bucket
	 * @param key
	 */
	public void keyDiscovered(String bucket, String key, long timestamp);
	
}
