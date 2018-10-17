package org.sagebionetworks.warehouse.workers.log;

import static org.junit.Assert.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.warehouse.workers.model.LogRecord;

public class WorkerLoggerImplTest {
	@Mock
	S3Logger mockS3Logger;
	@Mock
	CloudWatchLogger mockCloudWatchLogger;
	@Mock
	ProgressCallback<Void> mockProgressCallback;
	
	ArgumentCaptor<LogRecord> argCaptor;
	AmazonLoggerImpl logger;
	
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		this.logger = new AmazonLoggerImpl(mockS3Logger, mockCloudWatchLogger);
		argCaptor = ArgumentCaptor.forClass(LogRecord.class);
	}

	@Test
	public void testLog() {
		logger.logNonRetryableError(mockProgressCallback, "workerName", "exceptionName", "trace");
		verify(mockProgressCallback).progressMade(null);
		verify(mockS3Logger).log(eq(mockProgressCallback), argCaptor.capture());
		LogRecord s3Input = argCaptor.getValue();
		verify(mockCloudWatchLogger).log(eq(mockProgressCallback), argCaptor.capture());
		LogRecord cloudWatchInput = argCaptor.getValue();
		assertEquals(s3Input, cloudWatchInput);
		assertEquals("workerName", s3Input.getClassName());
		assertEquals("exceptionName", s3Input.getExceptionName());
		assertEquals("trace", s3Input.getStacktrace());
		assertNotNull(s3Input.getTimestamp());
	}

}
