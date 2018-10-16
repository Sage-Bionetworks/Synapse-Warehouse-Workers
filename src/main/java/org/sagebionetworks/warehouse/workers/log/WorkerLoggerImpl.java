package org.sagebionetworks.warehouse.workers.log;

import org.sagebionetworks.warehouse.workers.model.LogRecord;

import com.google.inject.Inject;

public class WorkerLoggerImpl implements WorkerLogger{
	
	private S3Logger s3Logger;
	private CloudWatchLogger cloudWatchLogger;
	
	@Inject
	public WorkerLoggerImpl(S3Logger s3Logger, CloudWatchLogger cloudWatchLogger) {
		this.s3Logger = s3Logger;
		this.cloudWatchLogger = cloudWatchLogger;
	}

	@Override
	public void logNonRetryableError(String workerName, String exceptionName, String stacktrace) {
		long timestamp = System.currentTimeMillis();
		LogRecord toLog = new LogRecord(timestamp, workerName, exceptionName, stacktrace);
		s3Logger.log(toLog);
		cloudWatchLogger.log(toLog);
	}

}
