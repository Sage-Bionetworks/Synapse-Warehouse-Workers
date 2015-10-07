package org.sagebionetworks.warehouse.workers.bucket;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.aws.utils.s3.BucketDao;
import org.sagebionetworks.warehouse.workers.BucketDaoProvider;
import org.sagebionetworks.warehouse.workers.db.FileManager;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public class BucketScanningWorkerTest {

	BucketDaoProvider mockBucketDaoProvider;
	FileManager mockFileManager;
	ProgressCallback<Void> mockProgressCallback;
	BucketDao bucketDaoOne;
	BucketDao bucketDaoTwo;
	Iterator<S3ObjectSummary> oneStream;
	Iterator<S3ObjectSummary> twoStream;
	
	BucketInfo bucketOne;
	BucketInfo bucketTwo;
	BucketInfoList bucketList;
	
	BucketScanningWorker worker;
	
	@Before
	public void before(){
		mockBucketDaoProvider = Mockito.mock(BucketDaoProvider.class);
		mockFileManager = Mockito.mock(FileManager.class);
		mockProgressCallback = Mockito.mock(ProgressCallback.class);
		bucketDaoOne = Mockito.mock(BucketDao.class);
		bucketDaoTwo = Mockito.mock(BucketDao.class);
		oneStream = Mockito.mock(Iterator.class);
		twoStream = Mockito.mock(Iterator.class);
	
		bucketOne = new BucketInfo();
		bucketOne.setBucketName("one");
		bucketTwo = new BucketInfo();
		bucketTwo.setBucketName("two");
		bucketList = new BucketInfoList(Arrays.asList(bucketOne, bucketTwo));
		
		worker = new BucketScanningWorker(mockBucketDaoProvider, bucketList, mockFileManager);
		
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
		verify(mockFileManager).addS3Objects(oneStream, mockProgressCallback);
		verify(mockFileManager).addS3Objects(twoStream, mockProgressCallback);
	}
}
