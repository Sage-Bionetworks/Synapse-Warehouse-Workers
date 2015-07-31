package org.sagebionetworks.warehouse.workers.model;

public class FileSubmissionMessage {

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
