package org.sagebionetworks.warehouse.workers.bucket;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.aws.utils.s3.BucketDao;
import org.sagebionetworks.warehouse.workers.BucketDaoProvider;
import org.sagebionetworks.warehouse.workers.db.FileManager;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.common.util.progress.ProgressCallback;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public class BucketScanningWorkerTest {
	@Mock
	BucketDaoProvider mockBucketDaoProvider;
	@Mock
	FileManager mockFileManager;
	@Mock
	ProgressCallback<Void> mockProgressCallback;
	@Mock
	BucketDao bucketDaoOne;
	@Mock
	BucketDao bucketDaoTwo;
	@Mock
	Iterator<S3ObjectSummary> oneStream;
	@Mock
	Iterator<S3ObjectSummary> twoStream;
	@Mock
	AmazonLogger mockWorkerLogger;
	
	BucketInfo bucketOne;
	BucketInfo bucketTwo;
	BucketInfoList bucketList;
	
	BucketScanningWorker worker;
	
	@Before
	public void before(){
		MockitoAnnotations.initMocks(this);
	
		bucketOne = new BucketInfo();
		bucketOne.setBucketName("one");
		bucketTwo = new BucketInfo();
		bucketTwo.setBucketName("two");
		bucketList = new BucketInfoList(Arrays.asList(bucketOne, bucketTwo));
		
		worker = new BucketScanningWorker(mockBucketDaoProvider, bucketList, mockFileManager, mockWorkerLogger);
		
		when(mockBucketDaoProvider.createBucketDao(bucketOne.getBucketName())).thenReturn(bucketDaoOne);
		when(mockBucketDaoProvider.createBucketDao(bucketTwo.getBucketName())).thenReturn(bucketDaoTwo);
		
		when(bucketDaoOne.summaryIterator(null)).thenReturn(oneStream);
		when(bucketDaoTwo.summaryIterator(null)).thenReturn(twoStream);
	}

	@Test
	public void testHappy() throws Exception{
		// call under test
		worker.run(mockProgressCallback);
		// The file manager should be passed each stream
		verify(mockFileManager).addS3Objects(oneStream, mockProgressCallback);
		verify(mockFileManager).addS3Objects(twoStream, mockProgressCallback);
	}

	@Test
	public void addS3ObjectsThrowsExceptions() throws Exception {
		doThrow(new IllegalArgumentException()).when(mockFileManager).addS3Objects(oneStream, mockProgressCallback);
		worker.run(mockProgressCallback);
		verify(mockWorkerLogger).logNonRetryableError(eq(mockProgressCallback),
				any(Void.class), eq("BucketScanningWorker"),any(IllegalArgumentException.class));
		verify(mockFileManager).addS3Objects(oneStream, mockProgressCallback);
		verify(mockFileManager).addS3Objects(twoStream, mockProgressCallback);
	}
}
