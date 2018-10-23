package org.sagebionetworks.warehouse.workers.log;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;
import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.warehouse.workers.BucketDaoProvider;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.model.LogRecord;
import org.sagebionetworks.warehouse.workers.utils.LogRecordUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.inject.Inject;

public class S3LoggerImpl implements S3Logger{
	public static final String TEMP_FILE_EXTENSION = ".txt.gz";
	public static final String TEMP_FILE_NAME = "workerLog";
	public static final String BUCKET_CONFIG_KEY = "org.sagebionetworks.warehouse.worker.log.bucket";
	
	private AmazonS3Client s3Client;
	private StreamResourceProvider resourceProvider;
	private String bucketName;
	
	@Inject
	public S3LoggerImpl(AmazonS3Client s3Client, StreamResourceProvider resourceProvider,
			Configuration config, BucketDaoProvider bucketDaoProvider) {
		this.s3Client = s3Client;
		this.resourceProvider = resourceProvider;
		this.bucketName = config.getProperty(BUCKET_CONFIG_KEY);
		// create the bucket if it does not exist
		s3Client.createBucket(bucketName);
	}

	@Override
	public <T> void log(ProgressCallback<T> progressCallback, T toCallback, LogRecord toLog) {
		if (!LogRecordUtils.isValidLogRecord(toLog)) {
			return;
		}
		String destinationKey = LogRecordUtils.getKey(toLog);
		String[] toWrite = LogRecordUtils.getFormattedLog(toLog);
		File logFile = resourceProvider.createTempFile(TEMP_FILE_NAME, TEMP_FILE_EXTENSION);
		PrintWriter writer = null;
		try {
			writer = resourceProvider.createGzipPrintWriter(logFile);
			resourceProvider.writeText(toWrite, writer);
			PutObjectRequest request = new PutObjectRequest(bucketName, destinationKey, logFile)
					// Both the object owner and the bucket owner get FULL_CONTROL over the object.
					.withCannedAcl(CannedAccessControlList.BucketOwnerFullControl);
			if (progressCallback != null) {
				progressCallback.progressMade(toCallback);
			}
			s3Client.putObject(request);
		} finally {
			if(writer != null){
				IOUtils.closeQuietly(writer);
			}
		}
	}

}
