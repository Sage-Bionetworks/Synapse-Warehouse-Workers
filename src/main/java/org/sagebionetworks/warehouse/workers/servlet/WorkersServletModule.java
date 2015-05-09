package org.sagebionetworks.warehouse.workers.servlet;

import org.sagebionetworks.warehouse.workers.AccessRecordBootstrap;
import org.sagebionetworks.warehouse.workers.AccessRecordBootstrapImpl;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.config.ConfigurationImpl;

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
		bind(AccessRecordBootstrap.class).to(AccessRecordBootstrapImpl.class);
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
