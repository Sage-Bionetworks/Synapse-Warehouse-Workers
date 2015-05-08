package org.sagebionetworks.warehouse.workers.servlet;

import javax.sql.DataSource;

import org.sagebionetworks.warehouse.workers.AccessRecordBootstrap;
import org.sagebionetworks.warehouse.workers.AccessRecordBootstrapImpl;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.config.ConfigurationImpl;
import org.sagebionetworks.warehouse.workers.db.ConnectionPool;
import org.sagebionetworks.warehouse.workers.db.ConnectionPoolImpl;
import org.sagebionetworks.warehouse.workers.db.FileMetadataDao;
import org.sagebionetworks.warehouse.workers.db.FileMetadataDaoImpl;
import org.sagebionetworks.warehouse.workers.semaphore.MultipleLockSemaphore;
import org.sagebionetworks.warehouse.workers.semaphore.MultipleLockSemaphoreImpl;
import org.springframework.jdbc.core.JdbcTemplate;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.google.inject.Provides;
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
		// Bind the path to the health check servlet.
		serve("/health/*").with(HealthCheckServlet.class);
		
		bind(Configuration.class).to(ConfigurationImpl.class);
		bind(ConnectionPool.class).to(ConnectionPoolImpl.class);
		bind(FileMetadataDao.class).to(FileMetadataDaoImpl.class);
		bind(AccessRecordBootstrap.class).to(AccessRecordBootstrapImpl.class);
		bind(MultipleLockSemaphore.class).to(MultipleLockSemaphoreImpl.class);
	}
	
	/**
	 * Create a new JdbcTemplate that uses the connection pool.
	 * 
	 * @param pool
	 * @return
	 */
	@Provides
	public JdbcTemplate createJdbcTemplate(ConnectionPool pool){
		return new JdbcTemplate(pool.getDataSource());
	}
	
	/**
	 * Gain direct access to the connection pool datasouce.s
	 * 
	 * @param pool
	 * @return
	 */
	@Provides
	public DataSource getDataSource(ConnectionPool pool){
		return pool.getDataSource();
	}
	
	/**
	 * Create a new AmazonSQSClient configured with the current AWS credentials.
	 * @return
	 */
	@Provides
	public AmazonSQSClient createSQSClient(Configuration configuration){
		return new AmazonSQSClient(configuration.getAWSCredentials());
	}

}
