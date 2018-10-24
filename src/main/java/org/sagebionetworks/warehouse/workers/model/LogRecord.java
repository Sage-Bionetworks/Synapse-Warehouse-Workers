package org.sagebionetworks.warehouse.workers.model;

public class LogRecord {

	private String className;
	private Throwable throwable;
	private long timestamp;
	
	public LogRecord(long timestamp, String className, Throwable throwable) {
		this.timestamp = timestamp;
		this.className = className;
		this.throwable = throwable;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "LogRecord [className=" + className + ", throwable=" + throwable + ", timestamp=" + timestamp + "]";
	}
}
