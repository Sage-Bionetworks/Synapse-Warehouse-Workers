package org.sagebionetworks.warehouse.workers.log;

import org.sagebionetworks.common.util.progress.ProgressCallback;

public interface AmazonLogger {

	/**
	 * Capture a non-retry-able error
	 * 
	 * @param progressCallback
	 * @param className
	 * @param exceptionName
	 * @param stacktrace
	 */
	public void logNonRetryableError(ProgressCallback<Void> progressCallback,
			String className, String exceptionName, String stacktrace);
}
