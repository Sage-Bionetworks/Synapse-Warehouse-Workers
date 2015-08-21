package org.sagebionetworks.warehouse.workers.bucket;

public class FileSubmissionMessage {

	public static final String ALIAS = "Message";
	String bucket;
	String key;

	public FileSubmissionMessage(String bucket, String key) {
		super();
		this.bucket = bucket;
		this.key = key;
	}

	public String getBucket() {
		return bucket;
	}

	public String getKey() {
		return key;
	}
}
