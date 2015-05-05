package org.sagebionetworks.warehouse.workers.config;

import com.amazonaws.auth.AWSCredentials;

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
	 * Get a configuration property by its key.
	 * @param key
	 * @return
	 */
	public String getProperty(String key);
}
