package org.sagebionetworks.warehouse.workers.log;

import org.sagebionetworks.warehouse.workers.model.LogRecord;

public interface S3Logger {

	/**
	 * Capture a log record in s3
	 * 
	 * @param toLog
	 */
	public void log(LogRecord toLog);
}
