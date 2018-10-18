package org.sagebionetworks.warehouse.workers.model;

public class LogRecord {

	private String className;
	private String exceptionName;
	private String stacktrace;
	private long timestamp;
	
	public LogRecord(long timestamp, String className, String exceptionName, String stacktrace) {
		this.timestamp = timestamp;
		this.className = className;
		this.exceptionName = exceptionName;
		this.stacktrace = stacktrace;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getExceptionName() {
		return exceptionName;
	}
	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}
	public String getStacktrace() {
		return stacktrace;
	}
	public void setStacktrace(String stacktrace) {
		this.stacktrace = stacktrace;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exceptionName == null) ? 0 : exceptionName.hashCode());
		result = prime * result + ((stacktrace == null) ? 0 : stacktrace.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + ((className == null) ? 0 : className.hashCode());
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
		LogRecord other = (LogRecord) obj;
		if (exceptionName == null) {
			if (other.exceptionName != null)
				return false;
		} else if (!exceptionName.equals(other.exceptionName))
			return false;
		if (stacktrace == null) {
			if (other.stacktrace != null)
				return false;
		} else if (!stacktrace.equals(other.stacktrace))
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "LogRecord [className=" + className + ", exceptionName=" + exceptionName + ", stacktrace=" + stacktrace
				+ ", timestamp=" + timestamp + "]";
	}
}
