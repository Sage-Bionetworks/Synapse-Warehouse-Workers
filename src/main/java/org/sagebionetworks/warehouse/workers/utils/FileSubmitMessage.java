package org.sagebionetworks.warehouse.workers.utils;

public class FileSubmitMessage {

	String bucket;
	String key;

	public FileSubmitMessage(String bucket, String key) {
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
