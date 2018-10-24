package org.sagebionetworks.warehouse.workers.snapshot;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.csv.utils.ObjectCSVReader;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.snapshot.AccessRecordDao;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordTestUtil;
import org.sagebionetworks.common.util.progress.ProgressCallback;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.model.Message;

public class SnapshotWriterTest {

	@Mock
	AmazonS3Client mockS3Client;
	@Mock
	AccessRecordDao mockDao;
	@Mock
	ProgressCallback<Message> mockCallback;
	@Mock
	StreamResourceProvider mockStreamResourceProvider;
	@Mock
	ObjectCSVReader<AccessRecord> mockObjectCSVReader;
	@Mock
	AmazonLogger mockAmazonLogger;
	
	Message message;
	String messageBody;
	List<AccessRecord> batch;

	AccessRecordWorker worker;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		worker = new AccessRecordWorker(mockS3Client, mockDao, mockStreamResourceProvider, mockAmazonLogger);

		messageBody = "<Message>\n"
				+"  <bucket>dev.access.record.sagebase.org</bucket>\n"
				+"  <key>0000000001/node/2015-07-30/23-34-16-308-e4ccd5c9-8f61-4043-bbe2-df6578b4672f.csv.gz</key>\n"
				+"</Message>";
		message = new Message();
		message.setBody(messageBody);

		Mockito.when(mockDao.doesPartitionExistForTimestamp(Mockito.anyLong())).thenReturn(true);
		batch = AccessRecordTestUtil.createValidAccessRecordBatch(5);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void writeEmptyListTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(null);
		SnapshotWriter.write(mockObjectCSVReader, mockDao, 2, mockCallback, message, worker, mockAmazonLogger);
		Mockito.verify(mockDao, Mockito.never()).insert((List<AccessRecord>) Mockito.any());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void writeInvalidRecordTest() throws IOException {
		AccessRecord invalidRecord = AccessRecordTestUtil.createValidAccessRecord();
		invalidRecord.setTimestamp(null);
		Mockito.when(mockObjectCSVReader.next()).thenReturn(invalidRecord, invalidRecord, null);
		SnapshotWriter.write(mockObjectCSVReader, mockDao, 2, mockCallback, message, worker, mockAmazonLogger);
		Mockito.verify(mockDao, Mockito.never()).insert((List<AccessRecord>) Mockito.any());
		Mockito.verify(mockAmazonLogger, times(2)).logNonRetryableError(eq(mockCallback), eq(message), eq(worker.getClass().getSimpleName()), any(Throwable.class));
	}

	@Test
	public void writeLessThanBatchSizeTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(batch.get(0), batch.get(1), batch.get(2), batch.get(3), null);
		SnapshotWriter.write(mockObjectCSVReader, mockDao, 5, mockCallback, message, worker, mockAmazonLogger);
		List<AccessRecord> expected = new ArrayList<AccessRecord>(Arrays.asList(batch.get(0), batch.get(1), batch.get(2), batch.get(3)));
		Mockito.verify(mockDao, Mockito.times(1)).insert(expected);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void writeBatchSizeTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(batch.get(0), batch.get(1), batch.get(2), batch.get(3), batch.get(4), null);
		SnapshotWriter.write(mockObjectCSVReader, mockDao, 5, mockCallback, message, worker, mockAmazonLogger);
		Mockito.verify(mockDao, Mockito.times(1)).insert((List<AccessRecord>) Mockito.any());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void writeOverBatchSizeTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(batch.get(0), batch.get(1), batch.get(2), batch.get(3), batch.get(4), null);
		SnapshotWriter.write(mockObjectCSVReader, mockDao, 3, mockCallback, message, worker, mockAmazonLogger);
		Mockito.verify(mockDao, Mockito.times(2)).insert((List<AccessRecord>) Mockito.any());
	}
}
