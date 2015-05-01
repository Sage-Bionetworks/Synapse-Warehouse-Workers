package org.sagebionetworks.warehouse.workers.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

public interface Configuration {
	
	public AWSCredentials getAWSCredentials();
	
	public AmazonS3Client getS3Client();
}
