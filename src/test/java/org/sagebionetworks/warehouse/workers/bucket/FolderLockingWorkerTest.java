package org.sagebionetworks.warehouse.workers.bucket;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.sagebionetworks.warehouse.workers.SemaphoreGatedRunnerProvider;
import org.sagebionetworks.warehouse.workers.collate.FolderLockingWorker;
import org.sagebionetworks.warehouse.workers.collate.LockedFolderRunner;
import org.sagebionetworks.warehouse.workers.model.FolderState;
import org.sagebionetworks.warehouse.workers.utils.XMLUtils;
import org.sagebionetworks.workers.util.progress.ProgressCallback;
import org.sagebionetworks.workers.util.progress.ProgressingRunner;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedRunner;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedRunnerConfiguration;

import com.amazonaws.services.sqs.model.Message;

public class FolderLockingWorkerTest {
	
	SemaphoreGatedRunnerProvider mockSemaphoreProvider;
	LockedFolderRunner mockCollateWorker;
	ProgressCallback<Message> mockProgressCallback;
	SemaphoreGatedRunner mockSemaphoreGatedRunner;
	Message mockMessage;
	FolderState folderDto;
	
	FolderLockingWorker worker;
	
	@Before
	public void before(){
		mockSemaphoreProvider = Mockito.mock(SemaphoreGatedRunnerProvider.class);
		mockSemaphoreGatedRunner = Mockito.mock(SemaphoreGatedRunner.class);
		mockCollateWorker = Mockito.mock(LockedFolderRunner.class);
		mockProgressCallback = Mockito.mock(ProgressCallback.class);
		mockMessage = Mockito.mock(Message.class);
		
		folderDto = new FolderState();
		folderDto.setBucket("someBcket");
		folderDto.setPath("somePath");
		// The FolderDto will be written to the message as XML.
		when(mockMessage.getBody()).thenReturn(XMLUtils.toXML(folderDto, FolderState.DTO_ALIAS));
		when(mockSemaphoreProvider.createRunner(any(SemaphoreGatedRunnerConfiguration.class))).thenReturn(mockSemaphoreGatedRunner);
		
		worker = new FolderLockingWorker(mockSemaphoreProvider, mockCollateWorker);
	}
	
	@Test
	public void testRun() throws Exception {
		// call under test.
		worker.run(mockProgressCallback, mockMessage);
		ArgumentCaptor<SemaphoreGatedRunnerConfiguration> configCapture = ArgumentCaptor.forClass(SemaphoreGatedRunnerConfiguration.class);
		verify(mockSemaphoreProvider).createRunner(configCapture.capture());
		// Validate the arguments
		ProgressCallback<Void> capturedProgressCallback = configCapture.getValue().getProgressCallack();
		assertNotNull(capturedProgressCallback);
		// calling progress on this callback should forward the event to the main callback
		capturedProgressCallback.progressMade(null);
		verify(mockProgressCallback).progressMade(mockMessage);
		
		ProgressingRunner<Void> capturedRunner = configCapture.getValue().getRunner();
		assertNotNull(capturedRunner);
		// calling run should run the collate worker
		capturedRunner.run(capturedProgressCallback);
		verify(mockCollateWorker).runWhileHoldingLock(capturedProgressCallback, folderDto);
		// check the rest of the config.
		assertEquals(folderDto.getBucket()+"-"+folderDto.getPath(), configCapture.getValue().getLockKey());
		assertEquals(1, configCapture.getValue().getMaxLockCount());
		assertEquals(FolderLockingWorker.TIMEOUT_SEC, configCapture.getValue().getLockTimeoutSec());
		
		// the semaphore gate should be run
		verify(mockSemaphoreGatedRunner).run();
		
	}

}
