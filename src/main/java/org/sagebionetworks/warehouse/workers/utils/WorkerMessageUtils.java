package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.warehouse.workers.bucket.XMLUtils;

public class WorkerMessageUtils {

	/**
	 * Generate a simple message to notify the topic that a file is ready to be processed.
	 * 
	 * @param bucket
	 * @param key
	 * @return
	 */
	public static String generateFileSubmitMessage(String bucket, String key) {
		FileSubmitMessage message = new FileSubmitMessage(bucket, key);
		return XMLUtils.toXML(message, "message");
	}
}
