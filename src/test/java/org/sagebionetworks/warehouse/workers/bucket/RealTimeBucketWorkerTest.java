package org.sagebionetworks.warehouse.workers.bucket;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.sagebionetworks.warehouse.workers.bucket.RealTimeBucketWorker;
import org.sagebionetworks.warehouse.workers.db.FileManager;
import org.sagebionetworks.warehouse.workers.utils.ClasspathUtils;
import org.sagebionetworks.common.util.progress.ProgressCallback;

import com.amazonaws.services.sqs.model.Message;

public class RealTimeBucketWorkerTest {

	FileManager mockFileManager;
	ProgressCallback<Message> mockProgressCallback;
	Message message;
	RealTimeBucketWorker worker;
	private String sampleEventJson;
	
	@SuppressWarnings("unchecked")
	@Before
	public void before(){
		sampleEventJson = ClasspathUtils.loadStringFromClassPath("SampleS3Event.json");
		mockFileManager = Mockito.mock(FileManager.class);
		mockProgressCallback = Mockito.mock(ProgressCallback.class);
		message = Mockito.mock(Message.class);
		when(message.getBody()).thenReturn(sampleEventJson);
		worker = new RealTimeBucketWorker(mockFileManager);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void tesHappy() throws Exception{
		worker.run(mockProgressCallback, message);
		// the manager should be passed the stream of messages
		verify(mockFileManager).addS3Objects(any(Iterator.class), any(ProgressCallback.class));
	}
}
