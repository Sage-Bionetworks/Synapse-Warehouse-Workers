package org.sagebionetworks.warehouse.workers.collate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.warehouse.workers.config.Configuration;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;

public class CollateFolderConfigurationProviderTest {
	@Mock
	CountingSemaphore mockSemaphore;
	@Mock
	AmazonSQSClient mockAwsSQSClient;
	@Mock
	AmazonSNSClient mockAwsSNSClient;
	@Mock
	CollateMessageQueue mockQueue;
	@Mock
	FolderLockingWorker mockWorker;
	@Mock
	Configuration mockConfiguration;
	CollateFolderConfigurationProvider provider;

	@Test
	public void testConstruction() {
		MockitoAnnotations.initMocks(this);
		Integer numberOfWorkers = 13;
		when(mockConfiguration.getProperty(CollateFolderConfigurationProvider.COLLATE_WORKER_COUNT_KEY)).thenReturn(numberOfWorkers.toString());
		provider = new CollateFolderConfigurationProvider(mockSemaphore, mockAwsSQSClient, mockAwsSNSClient, mockQueue, mockWorker, mockConfiguration);
		verify(mockConfiguration).getProperty(CollateFolderConfigurationProvider.COLLATE_WORKER_COUNT_KEY);
	}
}
