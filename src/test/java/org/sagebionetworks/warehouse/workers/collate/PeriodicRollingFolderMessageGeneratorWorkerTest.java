package org.sagebionetworks.warehouse.workers.collate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfo;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfoList;
import org.sagebionetworks.warehouse.workers.bucket.FolderDto;
import org.sagebionetworks.warehouse.workers.bucket.XMLUtils;
import org.sagebionetworks.warehouse.workers.db.FolderMetadataDao;
import org.sagebionetworks.warehouse.workers.db.FolderState;
import org.sagebionetworks.warehouse.workers.db.FolderState.State;
import org.sagebionetworks.workers.util.aws.message.MessageQueue;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.sqs.AmazonSQSClient;

public class PeriodicRollingFolderMessageGeneratorWorkerTest {
	
	FolderMetadataDao mockFolderDao;
	List<BucketInfo> bucketList;
	AmazonSQSClient mockAmazonSQSClient;
	MessageQueue mockMessageQueue;
	CollateMessageQueue collateQueue;
	ProgressCallback<Void> mockProgressCallback;
	String queueUrl;
	BucketInfo bucketInfo;
	List<FolderState> rollingList;
	
	PeriodicRollingFolderMessageGeneratorWorker worker;

	@Before
	public void before(){
		mockFolderDao = Mockito.mock(FolderMetadataDao.class);
		mockAmazonSQSClient = Mockito.mock(AmazonSQSClient.class);
		mockMessageQueue = Mockito.mock(MessageQueue.class);
		collateQueue = new CollateMessageQueue(mockMessageQueue);
		mockProgressCallback = Mockito.mock(ProgressCallback.class);
		queueUrl = "theQueueUrl";
		when(mockMessageQueue.getQueueUrl()).thenReturn(queueUrl);
		
		bucketInfo = new BucketInfo();
		bucketInfo.setBucketName("theBucket");
		bucketList = Arrays.asList(bucketInfo);
		
		FolderState state = new FolderState();
		state.setBucket(bucketInfo.getBucketName());
		state.setPath("pathOne");
		state.setState(State.ROLLING);
		state.setUpdatedOn(new Timestamp(1));
		rollingList = Arrays.asList(state);
		
		when(mockFolderDao.listFolders(bucketInfo.getBucketName(), FolderState.State.ROLLING)).thenReturn(rollingList.iterator());
		worker = new  PeriodicRollingFolderMessageGeneratorWorker(mockFolderDao, new BucketInfoList(bucketList), mockAmazonSQSClient, collateQueue);
	}
	
	@Test
	public void testRun() throws Exception{
		// call under test
		worker.run(mockProgressCallback);
		// The message should get sent
		String xml = XMLUtils.toXML(rollingList.get(0), FolderDto.FOLDER_DTO_ALIAS);
		verify(mockAmazonSQSClient, times(1)).sendMessage(queueUrl, xml);
		verify(mockProgressCallback, times(1)).progressMade(null);
	}

}
