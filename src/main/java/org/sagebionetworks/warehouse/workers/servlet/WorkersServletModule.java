package org.sagebionetworks.warehouse.workers.servlet;

import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.config.ConfigurationImpl;
import org.sagebionetworks.warehouse.workers.db.BasicDao;
import org.sagebionetworks.warehouse.workers.db.BasicDaoImpl;
import org.sagebionetworks.warehouse.workers.db.ConnectionPool;
import org.sagebionetworks.warehouse.workers.db.ConnectionPoolImpl;
import org.sagebionetworks.warehouse.workers.db.FileMetadataDao;
import org.sagebionetworks.warehouse.workers.db.FileMetadataDaoImpl;

import com.google.inject.servlet.ServletModule;

/**
 * Configures the servlets used by this app.
 * 
 * @author John
 *
 */
public class WorkersServletModule extends ServletModule {

	@Override
	protected void configureServlets() {
		super.configureServlets();
		
		serve("/health/*").with(HealthCheckServlet.class);
		
		bind(Configuration.class).to(ConfigurationImpl.class);
		bind(ConnectionPool.class).to(ConnectionPoolImpl.class);
		bind(BasicDao.class).to(BasicDaoImpl.class);
		bind(FileMetadataDao.class).to(FileMetadataDaoImpl.class);
	}

}
