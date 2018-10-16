package org.sagebionetworks.warehouse.workers.log;

public interface WorkerLogger {

	/**
	 * Capture a non-retry-able error
	 * 
	 * @param workerName
	 * @param exceptionName
	 * @param stacktrace
	 */
	public void logNonRetryableError(String workerName, String exceptionName, String stacktrace);
}
