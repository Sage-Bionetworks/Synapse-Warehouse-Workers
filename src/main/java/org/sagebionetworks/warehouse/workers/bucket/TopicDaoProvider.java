package org.sagebionetworks.warehouse.workers.bucket;

/**
 * This provider creates topics for each snapshot record and returns the topicArn.
 */
public interface TopicDaoProvider {

	/**
	 * Get the topicArn for the given topicName
	 * 
	 * @param topicName
	 * @effect create the topic if it does not exist
	 * @return
	 */
	public String getTopicArn(String topicName);

}
