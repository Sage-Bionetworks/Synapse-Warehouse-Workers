package org.sagebionetworks.warehouse.workers.config;

import java.util.List;

import org.sagebionetworks.warehouse.workers.WorkerStack;

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
	 * List all of the workers in this stack.
	 * @return
	 */
	public List<Class<? extends WorkerStack>> listAllWorkerStackInterfaces();
}
