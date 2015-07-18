package org.sagebionetworks.warehouse.workers.bucket;

/**
 * For each bucket there is a shared topic where messages are published about
 * S3Objects within the bucket.
 * 
 * @author John
 * 
 */
public interface BucketTopicPublisher {

	/**
	 * Publish a message about an S3 object to the shared topic for the object's
	 * bucket.
	 * 
	 * @param bucket
	 * @param key
	 */
	public void publishS3ObjectToTopic(String bucket, String key);

}
