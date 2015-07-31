package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.warehouse.workers.model.FileSubmissionMessage;


public class WorkerMessageUtils {

	/**
	 * Generate a simple message to notify the topic that a file is ready to be processed.
	 * 
	 * @param bucket
	 * @param key
	 * @return
	 */
	public static String generateFileSubmissionMessage(String bucket, String key) {
		FileSubmissionMessage message = new FileSubmissionMessage(bucket, key);
		return XMLUtils.toXML(message, "message");
	}
}
