package org.sagebionetworks.warehouse.workers.semaphore;

import static org.junit.Assert.*;

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
		long timeoutSec = 60;
		// get one lock
		String token1 = semaphore.attemptToAcquireLock(key, timeoutSec, 2);
		assertNotNull(token1);
		// get another
		String token2 = semaphore.attemptToAcquireLock(key, timeoutSec, 2);
		assertNotNull(token2);
		// Try for a third should not acquire a lock
		String token3 = semaphore.attemptToAcquireLock(key, timeoutSec, 2);
		assertEquals(null, token3);
		// release
		semaphore.releaseLock(key, token2);
		// we should now be able to get a new lock
		token3 = semaphore.attemptToAcquireLock(key, timeoutSec, 2);
		assertNotNull(token3);
	}

}
