package org.sagebionetworks.warehouse.workers.bucket;

import java.util.HashMap;
import java.util.Map;

import org.sagebionetworks.warehouse.workers.config.Configuration;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.google.inject.Inject;

public class TopicDaoProviderImpl implements TopicDaoProvider {

	private AmazonSNSClient snsClient;
	private static final String TOPIC_NAME_PATTERN = "%1$s-%2$s-record-topic";
	private Map<String, String> topicArnMap = new HashMap<String, String>();
	private String stack;

	@Inject
	TopicDaoProviderImpl(AmazonSNSClient snsClient, Configuration config) {
		super();
		this.snsClient = snsClient;
		this.stack = config.getProperty("org.sagebionetworks.warehouse.worker.stack");
	}

	@Override
	public String getTopicArn(String type) {
		if (topicArnMap.containsKey(type)) {
			return topicArnMap.get(type);
		}
		String topicName = String.format(TOPIC_NAME_PATTERN , this.stack, type);
		String topicArn = snsClient.createTopic(topicName).getTopicArn();
		topicArnMap.put(type, topicArn);
		return topicArn;
	}
}
