package org.sagebionetworks.warehouse.workers;

import org.sagebionetworks.aws.utils.s3.BucketDao;

/**
 * Simple abstraction for a BucketDao provider.
 *
 */
public interface BucketDaoProvider {
	
	/**
	 * Create a new BucketDao for a given bucket name.
	 * @param bucketName
	 * @return
	 */
	public BucketDao createBucketDao(String bucketName);

}
