package org.sagebionetworks.warehouse.workers;

import static org.mockito.Mockito.*;

import java.util.LinkedList;
import java.util.List;

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
	List<Class<? extends WorkerStack>> workers;
	ConnectionPool mockPool;
	
	@Before
	public void before(){
		mockInjector = Mockito.mock(Injector.class);
		mockConfig = Mockito.mock(Configuration.class);
		mockStack = Mockito.mock(WorkerStack.class);
		mockPool = Mockito.mock(ConnectionPool.class);
		when(mockInjector.getInstance(Configuration.class)).thenReturn(mockConfig);
		workers = new LinkedList<Class<? extends WorkerStack>>();
		workers.add(WorkerStack.class);
		when(mockConfig.listAllWorkerStackInterfaces()).thenReturn(workers);
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
