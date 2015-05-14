package org.sagebionetworks.warehouse.workers.semaphore;

/**
 * Indicates a lock has expired.
 *
 */
public class LockExpiredException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LockExpiredException(String message) {
		super(message);
	}
	

}
