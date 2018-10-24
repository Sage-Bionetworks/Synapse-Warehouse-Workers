package org.sagebionetworks.warehouse.workers.log;

import org.sagebionetworks.common.util.progress.ProgressCallback;

public interface AmazonLogger {

	/**
	 * Capture a non-retry-able error
	 * 
	 * @param progressCallback
	 * @param className - the name of the Java class where the error is captured
	 * @param throwable - the exception the was thrown
	 */
	public <T> void logNonRetryableError(ProgressCallback<T> progressCallback,
			T toCallback, String className, Throwable throwable);
}
