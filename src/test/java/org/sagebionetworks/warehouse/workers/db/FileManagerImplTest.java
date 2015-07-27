package org.sagebionetworks.warehouse.workers.db;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;
import org.sagebionetworks.warehouse.workers.bucket.BucketTopicPublisher;
import org.sagebionetworks.warehouse.workers.db.FileState.State;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public class FileManagerImplTest {
	
	FolderMetadataDao mockFolderMetadataDao;
	FileMetadataDao mockFileMetadataDao;
	BucketTopicPublisher mockBucketToTopicManager;
	ProgressCallback<Void> mockCallback;
	
	S3ObjectSummary rollingOne;
	S3ObjectSummary rollingTwo;
	
	S3ObjectSummary fileOne;
	S3ObjectSummary fileTwo;
	
	FileManagerImpl manger;
	
	@Before
	public void before(){
		mockFolderMetadataDao = Mockito.mock(FolderMetadataDao.class);
		mockFileMetadataDao = Mockito.mock(FileMetadataDao.class);
		mockBucketToTopicManager = Mockito.mock(BucketTopicPublisher.class);
		mockCallback = Mockito.mock(ProgressCallback.class);
		manger = new FileManagerImpl(mockFolderMetadataDao, mockFileMetadataDao, mockBucketToTopicManager);
		
		rollingOne = new S3ObjectSummary();
		rollingOne.setBucketName("bucketone");
		rollingOne.setKey(KeyGeneratorUtil.createNewKey(123, 1, true));
		
		rollingTwo = new S3ObjectSummary();
		rollingTwo.setBucketName("buckettwo");
		rollingTwo.setKey(KeyGeneratorUtil.createNewKey(456, 1, true));
		
		fileOne = new S3ObjectSummary();
		fileOne.setBucketName("bucketthree");
		fileOne.setKey(KeyGeneratorUtil.createNewKey(789, 1, false));
		
		fileTwo = new S3ObjectSummary();
		fileTwo.setBucketName("bucketfour");
		fileTwo.setKey(KeyGeneratorUtil.createNewKey(999, 1, false));
		
	}
	
	@Test
	public void testAddRollingFilesSamePath(){
		List<S3ObjectSummary> list = Arrays.asList(rollingOne, rollingOne);
		// call under test
		manger.addS3Objects(list.iterator(), mockCallback);
		// progress should be made for each file.
		verify(mockCallback, times(2)).progressMade(null);
		// Even though there are two files they have the same path so onl one call should be made.
		verify(mockFolderMetadataDao, times(1)).createOfUpdateFolderState(new FolderState(rollingOne.getBucketName(), "000000123/1970-01-01", FolderState.State.ROLLING, new Timestamp(1)));
	}

	@Test
	public void testAddRollingFilesMixedPath(){
		List<S3ObjectSummary> list = Arrays.asList(rollingOne, rollingTwo);
		// call under test
		manger.addS3Objects(list.iterator(), mockCallback);
		// progress should be made for each file.
		verify(mockCallback, times(2)).progressMade(null);
		// both paths should be marked as rolling.
		verify(mockFolderMetadataDao, times(1)).createOfUpdateFolderState(new FolderState(rollingOne.getBucketName(), "000000123/1970-01-01", FolderState.State.ROLLING, new Timestamp(1)));
		verify(mockFolderMetadataDao, times(1)).createOfUpdateFolderState(new FolderState(rollingTwo.getBucketName(), "000000456/1970-01-01", FolderState.State.ROLLING, new Timestamp(1)));
	}
	
	
	@Test
	public void testAddFileUnknown(){
		List<S3ObjectSummary> list = Arrays.asList(fileOne);
		// unknown state for a new file.
		FileState stateOne = new FileState();
		stateOne.setState(State.UNKNOWN);
		when(mockFileMetadataDao.getFileState(fileOne.getBucketName(), fileOne.getKey())).thenReturn(stateOne);
		// call under test
		manger.addS3Objects(list.iterator(), mockCallback);
		// progress should be made for each file.
		verify(mockCallback, times(1)).progressMade(null);
		verify(mockFileMetadataDao).setFileState(fileOne.getBucketName(), fileOne.getKey(), State.SUBMITTED);
		verify(mockBucketToTopicManager).publishS3ObjectToTopic(fileOne.getBucketName(), fileOne.getKey());
	}
	
	@Test
	public void testAddFileSubmitted(){
		List<S3ObjectSummary> list = Arrays.asList(fileOne);
		// submitted state for an exiting file.
		FileState stateOne = new FileState();
		stateOne.setState(State.SUBMITTED);
		when(mockFileMetadataDao.getFileState(fileOne.getBucketName(), fileOne.getKey())).thenReturn(stateOne);
		// call under test
		manger.addS3Objects(list.iterator(), mockCallback);
		// progress should be made for each file.
		verify(mockCallback, times(1)).progressMade(null);
		verify(mockFileMetadataDao, never()).setFileState(fileOne.getBucketName(), fileOne.getKey(), State.SUBMITTED);
		verify(mockBucketToTopicManager, never()).publishS3ObjectToTopic(fileOne.getBucketName(), fileOne.getKey());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testValidateNull(){
		FileManagerImpl.validateObjectSummary(null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testValidateBucketNull(){
		fileTwo.setBucketName(null);
		FileManagerImpl.validateObjectSummary(fileTwo);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testValidateKeyNull(){
		fileTwo.setKey(null);
		FileManagerImpl.validateObjectSummary(fileTwo);
	}
	
	@Test
	public void testValidateKeyHappy(){
		FileManagerImpl.validateObjectSummary(fileTwo);
	}
}
