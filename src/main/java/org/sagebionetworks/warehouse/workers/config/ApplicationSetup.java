package org.sagebionetworks.warehouse.workers.config;

import org.sagebionetworks.warehouse.workers.servlet.WorkersServletModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * This is the main application setup for all IoC.
 * 
 * @author John
 *
 */
public class ApplicationSetup extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new WorkersServletModule());
	}

}
