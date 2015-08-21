package org.sagebionetworks.warehouse.workers.snapshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.TeamSnapshotDao;
import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotUtils;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.sqs.model.Message;

public class TeamSnapshotWorkerTest {

	AmazonS3Client mockS3Client;
	TeamSnapshotDao mockDao;
	TeamSnapshotWorker worker;
	ProgressCallback<Message> mockCallback;
	Message message;
	String messageBody;
	StreamResourceProvider mockStreamResourceProvider;
	File mockFile;
	ObjectCSVReader<ObjectRecord> mockObjectCSVReader;
	List<ObjectRecord> batch;

	@SuppressWarnings("unchecked")
	@Before
	public void before() throws JSONObjectAdapterException {
		mockS3Client = Mockito.mock(AmazonS3Client.class);
		mockDao = Mockito.mock(TeamSnapshotDao.class);
		mockStreamResourceProvider = Mockito.mock(StreamResourceProvider.class);
		worker = new TeamSnapshotWorker(mockS3Client, mockDao, mockStreamResourceProvider);
		mockCallback = Mockito.mock(ProgressCallback.class);
		mockFile = Mockito.mock(File.class);
		mockObjectCSVReader = Mockito.mock(ObjectCSVReader.class);

		messageBody = "<Message>\n"
				+"  <bucket>bucket</bucket>\n"
				+"  <key>key</key>\n"
				+"</Message>";
		message = new Message();
		message.setBody(messageBody);

		Mockito.when(mockStreamResourceProvider.createTempFile(Mockito.eq(TeamSnapshotWorker.TEMP_FILE_NAME_PREFIX), Mockito.eq(TeamSnapshotWorker.TEMP_FILE_NAME_SUFFIX))).thenReturn(mockFile);
		Mockito.when(mockStreamResourceProvider.createObjectCSVReader(mockFile, ObjectRecord.class)).thenReturn(mockObjectCSVReader);

		batch = ObjectSnapshotTestUtil.createValidTeamSnapshotBatch(5);
	}

	@Test
	public void runTest() throws RecoverableMessageException, IOException {
		worker.run(mockCallback, message);
		Mockito.verify(mockStreamResourceProvider).createTempFile(Mockito.eq(TeamSnapshotWorker.TEMP_FILE_NAME_PREFIX), Mockito.eq(TeamSnapshotWorker.TEMP_FILE_NAME_SUFFIX));
		Mockito.verify(mockS3Client).getObject((GetObjectRequest) Mockito.any(), Mockito.eq(mockFile));
		Mockito.verify(mockStreamResourceProvider).createObjectCSVReader(mockFile, ObjectRecord.class);
		Mockito.verify(mockFile).delete();
		Mockito.verify(mockObjectCSVReader).close();
	}

	@Test
	public void deleteFileTest() throws RecoverableMessageException, IOException {
		Mockito.when(mockS3Client.getObject((GetObjectRequest) Mockito.any(), Mockito.eq(mockFile))).thenThrow(new AmazonClientException(""));
		try {
			worker.run(mockCallback, message);
		} catch (AmazonClientException e) {
			// expected
		}
		Mockito.verify(mockStreamResourceProvider).createTempFile(Mockito.eq(TeamSnapshotWorker.TEMP_FILE_NAME_PREFIX), Mockito.eq(TeamSnapshotWorker.TEMP_FILE_NAME_SUFFIX));
		Mockito.verify(mockS3Client).getObject((GetObjectRequest) Mockito.any(), Mockito.eq(mockFile));
		Mockito.verify(mockStreamResourceProvider, Mockito.never()).createObjectCSVReader(mockFile, ObjectRecord.class);
		Mockito.verify(mockFile).delete();
		Mockito.verify(mockObjectCSVReader, Mockito.never()).close();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void writeEmptyListTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(null);
		TeamSnapshotWorker.writeTeamSnapshot(mockObjectCSVReader, mockDao, 2);
		Mockito.verify(mockDao, Mockito.never()).insert((List<TeamSnapshot>) Mockito.any());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void writeInvalidRecordTest() throws IOException, JSONObjectAdapterException {
		ObjectRecord invalidRecord = ObjectSnapshotTestUtil.createValidTeamObjectRecord();
		invalidRecord.setTimestamp(null);
		Mockito.when(mockObjectCSVReader.next()).thenReturn(invalidRecord, invalidRecord, null);
		TeamSnapshotWorker.writeTeamSnapshot(mockObjectCSVReader, mockDao, 2);
		Mockito.verify(mockDao, Mockito.never()).insert((List<TeamSnapshot>) Mockito.any());
	}

	@Test
	public void writeLessThanBatchSizeTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(batch.get(0), batch.get(1), batch.get(2), batch.get(3), null);
		TeamSnapshotWorker.writeTeamSnapshot(mockObjectCSVReader, mockDao, 5);
		List<TeamSnapshot> expected = new ArrayList<TeamSnapshot>(Arrays.asList(
				ObjectSnapshotUtils.getTeamSnapshot(batch.get(0)),
				ObjectSnapshotUtils.getTeamSnapshot(batch.get(1)),
				ObjectSnapshotUtils.getTeamSnapshot(batch.get(2)),
				ObjectSnapshotUtils.getTeamSnapshot(batch.get(3))));
		Mockito.verify(mockDao, Mockito.times(1)).insert(expected);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void writeBatchSizeTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(batch.get(0), batch.get(1), batch.get(2), batch.get(3), batch.get(4), null);
		TeamSnapshotWorker.writeTeamSnapshot(mockObjectCSVReader, mockDao, 5);
		Mockito.verify(mockDao, Mockito.times(1)).insert((List<TeamSnapshot>) Mockito.any());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void writeOverBatchSizeTest() throws IOException {
		Mockito.when(mockObjectCSVReader.next()).thenReturn(batch.get(0), batch.get(1), batch.get(2), batch.get(3), batch.get(4), null);
		TeamSnapshotWorker.writeTeamSnapshot(mockObjectCSVReader, mockDao, 3);
		Mockito.verify(mockDao, Mockito.times(2)).insert((List<TeamSnapshot>) Mockito.any());
	}

}
