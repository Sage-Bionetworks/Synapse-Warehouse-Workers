package org.sagebionetworks.warehouse.workers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.warehouse.workers.semaphore.MultipleLockSemaphore;

public class SemaphoreGatedRunnerImplTest {

	MultipleLockSemaphore mockSemaphore;
	SemaphoreGatedRunnerImpl gate;
	ProgressingRunner mockRunner;
	String lockKey;
	long lockTimeoutSec;
	int maxLockCount;
	
	@Before
	public void before(){
		mockSemaphore = Mockito.mock(MultipleLockSemaphore.class);
		mockRunner = Mockito.mock(ProgressingRunner.class);
		lockKey = "aKey";
		lockTimeoutSec = 10;
		maxLockCount = 2;
		gate = new SemaphoreGatedRunnerImpl(mockSemaphore);
		gate.configure(mockRunner, lockKey, lockTimeoutSec, maxLockCount);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConfigureBad(){
		mockRunner = null;
		gate.configure(mockRunner, lockKey, lockTimeoutSec, maxLockCount);
	}
	
	@Test
	public void testHappy() throws Exception{
		String atoken = "atoken";
		when(mockSemaphore.attemptToAcquireLock(lockKey, lockTimeoutSec, maxLockCount)).thenReturn(atoken);
		// start the gate
		gate.run();
		// runner should be run
		verify(mockRunner).run(any(ProgressCallback.class));
		// The lock should get released.
		verify(mockSemaphore).releaseLock(lockKey, atoken);
		// The lock should not be refreshed for this case.
		verify(mockSemaphore, never()).refreshLockTimeout(anyString(), anyString(), anyLong());
	}
}
