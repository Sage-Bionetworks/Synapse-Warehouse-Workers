package org.sagebionetworks.warehouse.workers.snapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.AccessRecordDao;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordTestUtil;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.model.Message;

public class SnapshotWriterTest {

	AmazonS3Client mockS3Client;
	AccessRecordDao mockDao;
	AccessRecordWorker worker;
	ProgressCallback<Message> mockCallback;
	Message message;
	String messageBody;
	StreamResourceProvider mockStreamResourceProvider;
	ObjectCSVReader<AccessRecord> mockObjectCSVReader;
	List<AccessRecord> batch;

	@SuppressWarnings("unchecked")
	@Before
	public void before() {
		mockS3Client = Mockito.mock(AmazonS3Client.class);
		mockDao = Mockito.mock(AccessRecordDao.class);
		mockStreamResourceProvider = Mockito.mock(StreamResourceProvider.class);
		worker = new AccessRecordWorker(mockS3Client, mockDao, mockStreamResourceProvider);
		mockCallback = Mockito.mock(ProgressCallback.class);

		messageBody = "<Message>\n"
				+"  <bucket>dev.access.record.sagebase.org</bucket>\n"
				+"  <key>0000000001/node/2015-07-30/23-34-16-308-e4ccd5c9-8f61-4043-bbe2-df6578b4672f.csv.gz</key>\n"
				+"</Message>";
		message = new Message();
		message.setBody(messageBody);

		mockObjectCSVReader = Mockito.mock(ObjectCSVReader.class);
		Mockito.when(mockDao.doesPartitionExistForTimestamp(Mockito.anyLong())).thenReturn(true);
		batch = AccessRecordTestUtil.createValidAccessRecordBatch(5);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void writeEmptyListTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(null);
		SnapshotWriter.write(mockObjectCSVReader, mockDao, 2, mockCallback, message, worker);
		Mockito.verify(mockDao, Mockito.never()).insert((List<AccessRecord>) Mockito.any());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void writeInvalidRecordTest() throws IOException {
		AccessRecord invalidRecord = AccessRecordTestUtil.createValidAccessRecord();
		invalidRecord.setTimestamp(null);
		Mockito.when(mockObjectCSVReader.next()).thenReturn(invalidRecord, invalidRecord, null);
		SnapshotWriter.write(mockObjectCSVReader, mockDao, 2, mockCallback, message, worker);
		Mockito.verify(mockDao, Mockito.never()).insert((List<AccessRecord>) Mockito.any());
	}

	@Test
	public void writeLessThanBatchSizeTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(batch.get(0), batch.get(1), batch.get(2), batch.get(3), null);
		SnapshotWriter.write(mockObjectCSVReader, mockDao, 5, mockCallback, message, worker);
		List<AccessRecord> expected = new ArrayList<AccessRecord>(Arrays.asList(batch.get(0), batch.get(1), batch.get(2), batch.get(3)));
		Mockito.verify(mockDao, Mockito.times(1)).insert(expected);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void writeBatchSizeTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(batch.get(0), batch.get(1), batch.get(2), batch.get(3), batch.get(4), null);
		SnapshotWriter.write(mockObjectCSVReader, mockDao, 5, mockCallback, message, worker);
		Mockito.verify(mockDao, Mockito.times(1)).insert((List<AccessRecord>) Mockito.any());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void writeOverBatchSizeTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(batch.get(0), batch.get(1), batch.get(2), batch.get(3), batch.get(4), null);
		SnapshotWriter.write(mockObjectCSVReader, mockDao, 3, mockCallback, message, worker);
		Mockito.verify(mockDao, Mockito.times(2)).insert((List<AccessRecord>) Mockito.any());
	}
}
