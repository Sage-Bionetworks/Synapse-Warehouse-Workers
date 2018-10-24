package org.sagebionetworks.warehouse.workers.log;

import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.warehouse.workers.model.LogRecord;

import com.google.inject.Inject;

public class AmazonLoggerImpl implements AmazonLogger{
	
	private S3Logger s3Logger;
	private CloudWatchLogger cloudWatchLogger;
	
	@Inject
	public AmazonLoggerImpl(S3Logger s3Logger, CloudWatchLogger cloudWatchLogger) {
		this.s3Logger = s3Logger;
		this.cloudWatchLogger = cloudWatchLogger;
	}

	@Override
	public <T> void logNonRetryableError(ProgressCallback<T> progressCallback,
			T toCallback, String className, Throwable throwable) {
		long timestamp = System.currentTimeMillis();
		LogRecord toLog = new LogRecord(timestamp, className, throwable);
		if (progressCallback != null) {
			progressCallback.progressMade(null);
		}
		s3Logger.log(progressCallback, toCallback, toLog);
		cloudWatchLogger.log(progressCallback, toCallback, toLog);
	}

}
