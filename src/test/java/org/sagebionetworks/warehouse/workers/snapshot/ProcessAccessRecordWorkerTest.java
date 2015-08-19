package org.sagebionetworks.warehouse.workers.snapshot;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.ProcessedAccessRecordDao;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordTestUtil;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.sqs.model.Message;

public class ProcessAccessRecordWorkerTest {

	AmazonS3Client mockS3Client;
	ProcessedAccessRecordDao mockDao;
	ProcessAccessRecordWorker worker;
	ProgressCallback<Message> mockCallback;
	Message message;
	String messageBody;
	StreamResourceProvider mockStreamResourceProvider;
	File mockFile;
	ObjectCSVReader mockObjectCSVReader;
	List<AccessRecord> batch;

	@Before
	public void before() {
		mockS3Client = Mockito.mock(AmazonS3Client.class);
		mockDao = Mockito.mock(ProcessedAccessRecordDao.class);
		mockStreamResourceProvider = Mockito.mock(StreamResourceProvider.class);
		worker = new ProcessAccessRecordWorker(mockS3Client, mockDao, mockStreamResourceProvider);
		mockCallback = Mockito.mock(ProgressCallback.class);

		messageBody = "<Message>\n"
				+"  <bucket>bucket</bucket>\n"
				+"  <key>key</key>\n"
				+"</Message>";
		message = new Message();
		message.setBody(messageBody);

		mockFile = Mockito.mock(File.class);
		mockObjectCSVReader = Mockito.mock(ObjectCSVReader.class);
		Mockito.when(mockStreamResourceProvider.createTempFile(Mockito.eq("collatedAccessRecords"), Mockito.eq(".csv.gz"))).thenReturn(mockFile);
		Mockito.when(mockStreamResourceProvider.createObjectCSVReader(mockFile, AccessRecord.class)).thenReturn(mockObjectCSVReader);

		batch = AccessRecordTestUtil.createValidAccessRecordBatch(5);
	}

	@Test
	public void runTest() throws RecoverableMessageException, IOException {
		worker.run(mockCallback, message);
		Mockito.verify(mockStreamResourceProvider).createTempFile(Mockito.eq("collatedAccessRecords"), Mockito.eq(".csv.gz"));
		Mockito.verify(mockS3Client).getObject((GetObjectRequest) Mockito.any(), Mockito.eq(mockFile));
		Mockito.verify(mockStreamResourceProvider).createObjectCSVReader(mockFile, AccessRecord.class);
	}

	@Test
	public void writeInvalidRecordTest() throws IOException {
		AccessRecord invalidRecord = AccessRecordTestUtil.createInvalidAccessRecordWithNullTimestamp();
		Mockito.when(mockObjectCSVReader.next()).thenReturn(invalidRecord, null);
		ProcessAccessRecordWorker.writeProcessedAcessRecord(mockObjectCSVReader, mockDao, 2);
		Mockito.verify(mockDao, Mockito.never()).insert((List<ProcessedAccessRecord>) Mockito.any());
	}

	@Test
	public void writeLessThanBatchSizeTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(batch.get(0), batch.get(1), batch.get(2), batch.get(3), null);
		ProcessAccessRecordWorker.writeProcessedAcessRecord(mockObjectCSVReader, mockDao, 5);
		Mockito.verify(mockDao, Mockito.times(1)).insert((List<ProcessedAccessRecord>) Mockito.any());
	}

	@Test
	public void writeBatchSizeTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(batch.get(0), batch.get(1), batch.get(2), batch.get(3), batch.get(4), null);
		ProcessAccessRecordWorker.writeProcessedAcessRecord(mockObjectCSVReader, mockDao, 5);
		Mockito.verify(mockDao, Mockito.times(1)).insert((List<ProcessedAccessRecord>) Mockito.any());
	}

	@Test
	public void writeOverBatchSizeTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(batch.get(0), batch.get(1), batch.get(2), batch.get(3), batch.get(4), null);
		ProcessAccessRecordWorker.writeProcessedAcessRecord(mockObjectCSVReader, mockDao, 3);
		Mockito.verify(mockDao, Mockito.times(2)).insert((List<ProcessedAccessRecord>) Mockito.any());
	}
}