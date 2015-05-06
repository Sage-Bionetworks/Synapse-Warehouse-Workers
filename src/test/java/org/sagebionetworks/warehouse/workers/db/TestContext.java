package org.sagebionetworks.warehouse.workers.db;

import org.sagebionetworks.warehouse.workers.servlet.WorkersServletModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A simple singleton for tests to reuse an application context.
 * 
 *
 */
public class TestContext {

	private static Injector SINGLETON = Guice.createInjector(new WorkersServletModule());
	
	/**
	 * Get the singleton Injector.
	 * @return
	 */
	public static Injector singleton(){
		return SINGLETON;
	}

}
