package org.sagebionetworks.warehouse.workers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.warehouse.workers.config.ApplicationMain;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.ConnectionPool;

import com.google.inject.Injector;

public class ApplicationMainTest {
	
	Configuration mockConfig;
	WorkerStack mockStack;
	Injector mockInjector;
	ApplicationMain main;
	WorkerStackList stackList;
	ConnectionPool mockPool;
	
	@Before
	public void before(){
		mockInjector = Mockito.mock(Injector.class);
		mockConfig = Mockito.mock(Configuration.class);
		mockStack = Mockito.mock(WorkerStack.class);
		mockPool = Mockito.mock(ConnectionPool.class);
		when(mockStack.getWorketName()).thenReturn("MockWorkerStack");
		stackList = new WorkerStackList();
		stackList.add(mockStack);
		when(mockInjector.getInstance(WorkerStackList.class)).thenReturn(stackList);
		when(mockInjector.getInstance(WorkerStack.class)).thenReturn(mockStack);
		when(mockInjector.getInstance(ConnectionPool.class)).thenReturn(mockPool);		
		main = new ApplicationMain(mockInjector);
	}
	
	@Test
	public void testStart(){
		main.startup();
		// Each stack should be started.
		verify(mockStack).start();
	}
	
	@Test
	public void testShutdown(){
		main.startup();
		main.shutdown();
		// each stack should get shutdown.
		verify(mockStack).shutdown();
		// The connection pool should be closed.
		verify(mockPool).close();
	}

}
