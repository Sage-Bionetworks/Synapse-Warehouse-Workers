package org.sagebionetworks.warehouse.workers.bucket;

import org.sagebionetworks.aws.utils.s3.KeyData;
import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.google.inject.Inject;

public class BucketTopicPublisherImpl implements BucketTopicPublisher {

	private AmazonSNSClient snsClient;
	private TopicDaoProvider topicProvider;

	@Inject
	BucketTopicPublisherImpl(AmazonSNSClient snsClient, TopicDaoProvider topicProvider) {
		super();
		this.snsClient = snsClient;
		this.topicProvider = topicProvider;
	}

	@Override
	public void publishS3ObjectToTopic(String bucket, String key) {
		KeyData keyData = KeyGeneratorUtil.parseKey(key);
		String topicArn = topicProvider.getTopicArn(keyData.getType());
		String message = generateMessage(bucket, key);
		snsClient.publish(topicArn, message);
	}

	/**
	 * Generate a simple message to notify the topic that a file is ready to be processed.
	 * 
	 * @param bucket
	 * @param key
	 * @return
	 */
	private String generateMessage(String bucket, String key) {
		SnapshotRecordMessage message = new SnapshotRecordMessage(bucket, key);
		return XMLUtils.toXML(message, "message");
	}

	public class SnapshotRecordMessage {
		String bucket;
		String key;
		public SnapshotRecordMessage(String bucket, String key) {
			super();
			this.bucket = bucket;
			this.key = key;
		}
	}
}
