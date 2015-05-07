package org.sagebionetworks.warehouse.workers.config;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;

/**
 * Abstraction for all of the configuration properties.
 *
 */
public interface Configuration {
	
	/**
	 * Get the AWS credentials to be used for the entire application.
	 * 
	 * @return
	 */
	public AWSCredentials getAWSCredentials();
	
	/**
	 * Create a new AmazonSQSClient configured with the current AWS credentials.
	 * @return
	 */
	public AmazonSQSClient createSQSClient();
	
	/**
	 * Get a configuration property by its key.
	 * @param key
	 * @return
	 */
	public String getProperty(String key);
	
	/**
	 * Get the list of database class names.
	 * 
	 * @return
	 */
	List<String> getDatabaseObjectClassNames();
}
