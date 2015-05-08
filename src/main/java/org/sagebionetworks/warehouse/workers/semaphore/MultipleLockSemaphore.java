package org.sagebionetworks.warehouse.workers.semaphore;

/**
 * A Database backed semaphore that supports multiple locks to be issued for the
 * same key.
 * 
 * @author John
 * 
 */
public interface MultipleLockSemaphore {

	/**
	 * Attempt to acquire a lock with the given key. This method is non-blocking
	 * and a lock is either available and issued immediately or not at all.
	 * 
	 * @param key
	 *            A unique key to lock on
	 * @param maxLockCount
	 *            The maximum number of locks of that can be issued to the given
	 *            key.
	 * @param timeoutSec
	 *            The maximum life of the lock in seconds. If the lock is not
	 *            released before this amount of time elapses then it will be
	 *            forfeit.
	 * 
	 * @return The token for the lock. This token must be used to release the
	 *         lock. Returns null when no locks are available.
	 */
	public String attemptToAcquireLock(String key, long timeoutSec,
			int maxLockCount);

	/**
	 * Release a lock using the token that was issued when the lock was
	 * acquired.
	 * 
	 * @param key The same key that was used to acquire the lock.
	 * @param token
	 * @return True if the lock release was successful. False if the was not
	 *         released because it already timed out and was forcefully released.
	 */
	public boolean releaseLock(String key, String token);
	
	/**
	 * Force the release of all locks.
	 */
	public void releaseAllLocks();

}
