package org.sagebionetworks.warehouse.workers;

import org.sagebionetworks.aws.utils.s3.BucketDao;
import org.sagebionetworks.aws.utils.s3.BucketDaoImpl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

public class BucketDaoProviderImpl implements BucketDaoProvider {
	
	private AmazonS3Client awsS3Client;
	
	@Inject
	public BucketDaoProviderImpl(AmazonS3Client awsS3Client) {
		super();
		this.awsS3Client = awsS3Client;
	}

	@Override
	public BucketDao createBucketDao(String bucketName) {
		return new BucketDaoImpl(awsS3Client, bucketName);
	}

}
