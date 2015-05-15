package org.sagebionetworks.warehouse.workers.semaphore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.db.TestContext;

/**
 * This is a database level integration test for the MultipleLockSemaphore.
 * 
 * @author John
 *
 */
public class MultipleLockSemaphoreImplTest {
	
	MultipleLockSemaphore semaphore = TestContext.singleton().getInstance(MultipleLockSemaphore.class);
	
	@Before
	public void before(){
		semaphore.releaseAllLocks();
	}
	
	@Test
	public void testAcquireRelease(){
		String key = "sampleKey";
		int maxLockCount = 2;
		long timeoutSec = 60;
		// get one lock
		String token1 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertNotNull(token1);
		// get another
		String token2 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertNotNull(token2);
		// Try for a third should not acquire a lock
		String token3 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertEquals(null, token3);
		// release
		semaphore.releaseLock(key, token2);
		// we should now be able to get a new lock
		token3 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertNotNull(token3);
	}
	
	@Test
	public void testLockExpired() throws InterruptedException{
		String key = "sampleKey";
		int maxLockCount = 1;
		long timeoutSec = 1;
		// get one lock
		String token1 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertNotNull(token1);
		// Should not be able to get a lock
		String token2 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertEquals(null, token2);
		// Wait for the lock first lock to expire
		Thread.sleep(timeoutSec*1000*2);
		// We should now be able to get the lock as the first is expired.
		token2 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertNotNull(token2);
	}
	
	@Test (expected=LockExpiredException.class)
	public void testReleaseExpiredLock() throws InterruptedException{
		String key = "sampleKey";
		int maxLockCount = 1;
		long timeoutSec = 1;
		// get one lock
		String token1 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertNotNull(token1);
		// Wait until the lock expires
		Thread.sleep(timeoutSec*1000*2);
		// another should be able to get the lock
		String token2 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertNotNull(token2);
		// this should fail as the lock has already expired.
		semaphore.releaseLock(key, token1);
	}
	
	@Test
	public void testRefreshLockTimeout() throws InterruptedException{
		String key = "sampleKey";
		int maxLockCount = 1;
		long timeoutSec = 2;
		// get one lock
		String token1 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertNotNull(token1);
		// We should be able to refresh the lock.
		for(int i=0; i< timeoutSec+1; i++){
			semaphore.refreshLockTimeout(key, token1, timeoutSec);
			Thread.sleep(1000);
		}
		// The lock should still be held even though we have now exceeded to original timeout.
		semaphore.releaseLock(key, token1);
	}
	
	@Test (expected=LockExpiredException.class)
	public void testRefreshExpiredLock() throws InterruptedException{
		String key = "sampleKey";
		int maxLockCount = 1;
		long timeoutSec = 1;
		// get one lock
		String token1 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertNotNull(token1);
		// Wait until the lock expires
		Thread.sleep(timeoutSec*1000*2);
		// another should be able to get the lock
		String token2 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertNotNull(token2);
		// this should fail as the lock has already expired.
		semaphore.refreshLockTimeout(key, token1, timeoutSec);
	}

	@Test (expected=LockExpiredException.class)
	public void testReleaseLockAfterReleaseAllLocks(){
		String key = "sampleKey";
		int maxLockCount = 1;
		long timeoutSec = 1;
		// get one lock
		String token1 = semaphore.attemptToAcquireLock(key, timeoutSec, maxLockCount);
		assertNotNull(token1);
		// Force the release of all locks
		semaphore.releaseAllLocks();
		// Now try to release the lock
		semaphore.releaseLock(key, token1);
	}
}
