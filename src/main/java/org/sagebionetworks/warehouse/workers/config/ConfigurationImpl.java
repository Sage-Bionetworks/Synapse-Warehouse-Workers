package org.sagebionetworks.warehouse.workers.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Singleton;

@Singleton
public class ConfigurationImpl implements Configuration {


	public AWSCredentials getAWSCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	public AmazonS3Client getS3Client() {
		// TODO Auto-generated method stub
		return null;
	}

}
