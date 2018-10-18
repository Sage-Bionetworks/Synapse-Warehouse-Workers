package org.sagebionetworks.warehouse.workers.log;

import org.sagebionetworks.common.util.progress.ProgressCallback;

public interface AmazonLogger {

	/**
	 * Capture a non-retry-able error
	 * 
	 * @param progressCallback
	 * @param className - the name of the Java class where the error is captured
	 * @param exceptionName - the name of the exception
	 * @param stacktrace - the exception's stacktrace
	 */
	public <T> void logNonRetryableError(ProgressCallback<T> progressCallback,
			T toCallback, String className, String exceptionName, String stacktrace);
}
