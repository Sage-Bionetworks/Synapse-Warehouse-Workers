package org.sagebionetworks.warehouse.workers.bucket;

import java.util.HashMap;
import java.util.Map;

import org.sagebionetworks.warehouse.workers.config.Configuration;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TopicDaoProviderImpl implements TopicDaoProvider {

	private AmazonSNSClient snsClient;
	private Configuration config;
	private static final String TOPIC_NAME_PATTERN = "%1$s-%2$s-record-topic";
	private Map<String, String> topicArnMap = new HashMap<String, String>();

	@Inject
	TopicDaoProviderImpl(AmazonSNSClient snsClient, Configuration config) {
		super();
		this.snsClient = snsClient;
		this.config = config;
	}

	@Override
	public String getTopicArn(String type) {
		if (topicArnMap.containsKey(type)) {
			return topicArnMap.get(type);
		}
		String topicArn = snsClient.createTopic(getTopicName(type)).getTopicArn();
		topicArnMap.put(type, topicArn);
		return topicArn;
	}

	/**
	 * Generate the topic name from stack and type
	 * 
	 * @param type
	 * @return
	 */
	private String getTopicName(String type) {
		String stack = config.getProperty("org.sagebionetworks.warehouse.worker.stack");
		return String.format(TOPIC_NAME_PATTERN , stack, type);
	}

}
