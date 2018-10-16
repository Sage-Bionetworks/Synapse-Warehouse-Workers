package org.sagebionetworks.warehouse.workers.log;

import static org.junit.Assert.*;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.warehouse.workers.model.LogRecord;

public class WorkerLoggerImplTest {
	@Mock
	S3Logger mockS3Logger;
	@Mock
	CloudWatchLogger mockCloudWatchLogger;
	
	ArgumentCaptor<LogRecord> argCaptor;
	WorkerLoggerImpl logger;
	
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		this.logger = new WorkerLoggerImpl(mockS3Logger, mockCloudWatchLogger);
		argCaptor = ArgumentCaptor.forClass(LogRecord.class);
	}

	@Test
	public void testLog() {
		logger.logNonRetryableError("workerName", "exceptionName", "trace");
		verify(mockS3Logger).log(argCaptor.capture());
		LogRecord s3Input = argCaptor.getValue();
		verify(mockCloudWatchLogger).log(argCaptor.capture());
		LogRecord cloudWatchInput = argCaptor.getValue();
		assertEquals(s3Input, cloudWatchInput);
		assertEquals("workerName", s3Input.getWorkerName());
		assertEquals("exceptionName", s3Input.getExceptionName());
		assertEquals("trace", s3Input.getStacktrace());
		assertNotNull(s3Input.getTimestamp());
	}

}
