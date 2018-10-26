package org.sagebionetworks.warehouse.workers.log;

import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.warehouse.workers.model.LogRecord;

public interface CloudWatchLogger {

	/**
	 * Capture a log record in CloudWatch
	 * 
	 * @param progressCallback
	 * @param toLog
	 */
	public <T> void log(ProgressCallback<T> progressCallback, T toCallback, LogRecord toLog);
}
