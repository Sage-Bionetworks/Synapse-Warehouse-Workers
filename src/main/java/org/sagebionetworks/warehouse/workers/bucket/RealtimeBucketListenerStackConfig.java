package org.sagebionetworks.warehouse.workers.bucket;

public class RealtimeBucketListenerStackConfig {
	
	private String topicName;
	private String queueName;
	
	/**
	 * The name of the topic where bucket events will be published too.
	 * @return
	 */
	public String getTopicName() {
		return topicName;
	}
	
	/**
	 * The name of the topic where bucket events will be published too.
	 * @param topicName
	 */
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	/**
	 * The name of the queue where bucket events from the topic will be pushed.
	 * @return
	 */
	public String getQueueName() {
		return queueName;
	}
	/**
	 * The name of the queue where bucket events from the topic will be pushed.
	 * @param queueName
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((queueName == null) ? 0 : queueName.hashCode());
		result = prime * result
				+ ((topicName == null) ? 0 : topicName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RealtimeBucketListenerStackConfig other = (RealtimeBucketListenerStackConfig) obj;
		if (queueName == null) {
			if (other.queueName != null)
				return false;
		} else if (!queueName.equals(other.queueName))
			return false;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "RealtimeBucketListenerStackConfig [topicName=" + topicName
				+ ", queueName=" + queueName + "]";
	}
	
}
