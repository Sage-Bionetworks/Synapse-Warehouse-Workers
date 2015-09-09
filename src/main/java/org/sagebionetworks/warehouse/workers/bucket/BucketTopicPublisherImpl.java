package org.sagebionetworks.warehouse.workers.bucket;

import org.joda.time.DateTime;
import org.sagebionetworks.aws.utils.s3.KeyData;
import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.utils.XMLUtils;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.google.inject.Inject;

public class BucketTopicPublisherImpl implements BucketTopicPublisher {

	private AmazonSNSClient snsClient;
	private TopicDaoProvider topicProvider;
	private Configuration config;

	@Inject
	BucketTopicPublisherImpl(AmazonSNSClient snsClient, TopicDaoProvider topicProvider, Configuration config) {
		super();
		this.snsClient = snsClient;
		this.topicProvider = topicProvider;
		this.config = config;
	}

	@Override
	public void publishS3ObjectToTopic(String bucket, String key) {
		KeyData keyData = KeyGeneratorUtil.parseKey(key);
		DateTime dataDate = new DateTime(keyData.getTimeMS());
		if (dataDate.isBefore(config.getStartDate().getMillis()) ||
				dataDate.isAfter(config.getEndDate().getMillis())) {
			// ignore invalid time snapshots
			return;
		}
		String topicArn = topicProvider.getTopicArn(keyData.getType());
		FileSubmissionMessage dto = new FileSubmissionMessage(bucket, key);
		String message = XMLUtils.toXML(dto, "Message");
		snsClient.publish(topicArn, message);
	}

}
