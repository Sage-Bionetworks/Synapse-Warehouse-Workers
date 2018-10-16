package org.sagebionetworks.warehouse.workers;

import org.sagebionetworks.warehouse.workers.config.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Binding for AWS resources.
 *
 */
public class AwsModule extends AbstractModule {

	@Override
	protected void configure() {

	}
	
	@Provides @Singleton
	public AWSCredentials getCredentials(Configuration config){
		return new BasicAWSCredentials(config.getProperty("org.sagebionetworks.stack.iam.id"), config.getProperty("org.sagebionetworks.stack.iam.key"));
	}
	
	@Provides @Singleton
	public AmazonS3Client getS3Client(AWSCredentials credentials){
		return new AmazonS3Client(credentials);
	}
	
	@Provides @Singleton
	public AmazonSQSClient getSQSClient(AWSCredentials credentials){
		return new AmazonSQSClient(credentials);
	}
	
	@Provides @Singleton
	public AmazonSNSClient getSNSClient(AWSCredentials credentials){
		return new AmazonSNSClient(credentials);
	}
	
	@Provides @Singleton
	public AmazonCloudWatchClient getCloudWatchClient(AWSCredentials credentials){
		return new AmazonCloudWatchClient(credentials);
	}

}
