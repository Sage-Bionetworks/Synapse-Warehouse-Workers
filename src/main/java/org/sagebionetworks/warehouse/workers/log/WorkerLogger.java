package org.sagebionetworks.warehouse.workers.log;

import org.sagebionetworks.common.util.progress.ProgressCallback;

public interface WorkerLogger {

	/**
	 * Capture a non-retry-able error
	 * 
	 * @param progressCallback
	 * @param workerName
	 * @param exceptionName
	 * @param stacktrace
	 */
	public void logNonRetryableError(ProgressCallback<Void> progressCallback, String workerName, String exceptionName, String stacktrace);
}
