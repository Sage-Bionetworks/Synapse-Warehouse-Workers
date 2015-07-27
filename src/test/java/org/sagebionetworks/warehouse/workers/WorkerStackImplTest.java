package org.sagebionetworks.warehouse.workers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class WorkerStackImplTest {

	Runnable mockRunnable;
	WorkerStackConfiguration config;
	int periodMS;
	int startDelayMS;
	
	@Before
	public void before(){
		mockRunnable = Mockito.mock(Runnable.class);
		config = new WorkerStackConfiguration();
		periodMS = 100;
		startDelayMS = 10;
		config.setPeriodMS(periodMS);
		config.setStartDelayMs(startDelayMS);
		config.setWorkerName("A sample worker");
		config.setRunner(mockRunnable);
	}
	
	@Test
	public void testHappy() throws InterruptedException{
		WorkerStackImpl stack = new WorkerStackImpl(config);
		try{
			stack.start();
			// wait for the timer to fire
			Thread.sleep(periodMS*2);
			verify(mockRunnable, atLeast(1)).run();
		}finally{
			stack.shutdown();
		}		
	}
	
	@Test
	public void testException() throws InterruptedException{
		// setup the runner to throw an exception ever time.
		doThrow(new RuntimeException("Something went wrong")).when(mockRunnable).run();
		WorkerStackImpl stack = new WorkerStackImpl(config);
		try{
			stack.start();
			// wait for the timer to fire at least 4 times.
			Thread.sleep(periodMS*4);
			// the timer should call the runner twice even though the first threw an exception.
			verify(mockRunnable, atLeast(2)).run();
		}finally{
			stack.shutdown();
		}		
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNullConfig(){
		new WorkerStackImpl(null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testRunnerNull(){
		config.setRunner(null);
		new WorkerStackImpl(config);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNameNull(){
		config.setWorkerName(null);
		new WorkerStackImpl(config);
	}

}
