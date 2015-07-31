package org.sagebionetworks.warehouse.workers.bucket;

import org.sagebionetworks.aws.utils.s3.KeyData;
import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;
import org.sagebionetworks.warehouse.workers.utils.WorkerMessageUtils;

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
		String message = WorkerMessageUtils.generateFileSubmissionMessage(bucket, key);
		snsClient.publish(topicArn, message);
	}

}
