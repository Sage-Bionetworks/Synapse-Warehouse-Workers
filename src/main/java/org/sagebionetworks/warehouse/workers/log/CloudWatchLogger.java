package org.sagebionetworks.warehouse.workers.log;

import org.sagebionetworks.warehouse.workers.model.LogRecord;

public interface CloudWatchLogger {

	/**
	 * Capture a log record in CloudWatch
	 * 
	 * @param toLog
	 */
	public void log(LogRecord toLog);
}
