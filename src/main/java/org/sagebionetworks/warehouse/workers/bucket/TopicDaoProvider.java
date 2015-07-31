package org.sagebionetworks.warehouse.workers.bucket;

/**
 * This provider creates topics for each snapshot record type and returns the topicArn.
 */
public interface TopicDaoProvider {

	/**
	 * Get the topicArn for the given snapshot record type
	 * 
	 * @param type
	 * @effect create the topic if it does not exist
	 * @return
	 */
	public String getTopicArn(String type);
}
