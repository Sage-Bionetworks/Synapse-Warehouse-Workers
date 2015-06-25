package org.sagebionetworks.warehouse.workers.db;

import org.sagebionetworks.warehouse.workers.AwsModule;
import org.sagebionetworks.warehouse.workers.WorkersModule;
import org.sagebionetworks.warehouse.workers.servlet.WorkersServletModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A simple singleton for tests to reuse an application context.
 * 
 *
 */
public class TestContext {

	private static Injector SINGLETON = Guice.createInjector(
			new WorkersServletModule(), new DatabaseModule(),
			new WorkersModule(), new AwsModule());

	/**
	 * Get the singleton Injector.
	 * 
	 * @return
	 */
	public static Injector singleton() {
		return SINGLETON;
	}

}
