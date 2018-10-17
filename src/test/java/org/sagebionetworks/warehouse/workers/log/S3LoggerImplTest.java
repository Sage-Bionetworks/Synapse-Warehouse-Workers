package org.sagebionetworks.warehouse.workers.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sagebionetworks.warehouse.workers.log.S3LoggerImpl.BUCKET_CONFIG_KEY;
import static org.sagebionetworks.warehouse.workers.log.S3LoggerImpl.TEMP_FILE_NAME;

import java.io.File;
import java.io.PrintWriter;

import static org.sagebionetworks.warehouse.workers.log.S3LoggerImpl.TEMP_FILE_EXTENSION;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.warehouse.workers.BucketDaoProvider;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.model.LogRecord;
import org.sagebionetworks.warehouse.workers.utils.LogRecordUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3LoggerImplTest {
	@Mock
	AmazonS3Client mockS3Client;
	@Mock
	StreamResourceProvider mockResourceProvider;
	@Mock
	Configuration mockConfig;
	@Mock
	BucketDaoProvider mockBucketDaoProvider;
	@Mock
	ProgressCallback<Void> mockProgressCallback;
	@Mock
	File mockFile;
	@Mock
	PrintWriter mockWriter;
	
	ArgumentCaptor<PutObjectRequest> captor;
	S3LoggerImpl s3Logger;
	String bucketName = "test.warehouse.workers.log.sagebase.org";
	
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		when(mockConfig.getProperty(BUCKET_CONFIG_KEY)).thenReturn(bucketName);
		when(mockResourceProvider.createTempFile(TEMP_FILE_NAME, TEMP_FILE_EXTENSION)).thenReturn(mockFile);
		when(mockResourceProvider.createGzipPrintWriter(mockFile)).thenReturn(mockWriter);
		s3Logger = new S3LoggerImpl(mockS3Client, mockResourceProvider, mockConfig, mockBucketDaoProvider);
		captor = ArgumentCaptor.forClass(PutObjectRequest.class);
	}

	@Test
	public void testConstructor() {
		verify(mockConfig).getProperty(BUCKET_CONFIG_KEY);
		verify(mockBucketDaoProvider).createBucketDao(bucketName);
	}

	@Test
	public void testLog() {
		long timestamp = System.currentTimeMillis();
		LogRecord toLog = new LogRecord(timestamp, "workerName", "exceptionName", "trace");
		s3Logger.log(mockProgressCallback, toLog);
		verify(mockResourceProvider).createTempFile(TEMP_FILE_NAME, TEMP_FILE_EXTENSION);
		verify(mockResourceProvider).createGzipPrintWriter(mockFile);
		verify(mockResourceProvider).writeText(LogRecordUtils.getFormattedLog(toLog), mockWriter);
		verify(mockProgressCallback).progressMade(null);
		verify(mockS3Client).putObject(captor.capture());
		PutObjectRequest request = captor.getValue();
		assertNotNull(request);
		assertEquals(bucketName, request.getBucketName());
		// only check the prefix 00-00-00-000 (hh-mm-ss-mss)
		assertTrue(request.getKey().startsWith(LogRecordUtils.getKey(toLog).substring(0, 11)));
		assertEquals(mockFile, request.getFile());
		assertEquals(CannedAccessControlList.BucketOwnerFullControl, request.getCannedAcl());
		verify(mockWriter).close();
	}
	
	@Test
	public void testInvalidLogRecord() {
		s3Logger.log(mockProgressCallback, null);
		verify(mockResourceProvider, never()).createTempFile(any(String.class), any(String.class));
		verify(mockResourceProvider, never()).createGzipPrintWriter(any(File.class));
		verify(mockResourceProvider, never()).writeText(any(String[].class), any(PrintWriter.class));
		verify(mockProgressCallback, never()).progressMade(null);
		verify(mockS3Client, never()).putObject(any(PutObjectRequest.class));
		verify(mockWriter, never()).close();
	}
}
