package org.sagebionetworks.warehouse.workers.snapshot;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.csv.utils.ObjectCSVReader;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.HasPartitions;
import org.sagebionetworks.warehouse.workers.db.snapshot.SnapshotDao;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.common.util.progress.ProgressCallback;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.sqs.model.Message;

public class AbstractSnapshotWorkerTest {

	class StringWithPartitionsDao implements HasPartitions, SnapshotDao<String> {

		@Override
		public void insert(List<String> batch) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean doesPartitionExistForTimestamp(long timeMS) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

	class IntToStringWorker extends AbstractSnapshotWorker<Integer, String> {

		public IntToStringWorker(AmazonS3Client s3Client, SnapshotDao<String> dao,
				StreamResourceProvider streamResourceProvider, AmazonLogger logger) {
			super(s3Client, dao, streamResourceProvider, logger);
			log = LogManager.getLogger(IntToStringWorker.class);
			tempFileNamePrefix = "prefix";
			tempFileNameSuffix = "suffix";
			snapshotHeader = new String[]{"int"};
			clazz = Integer.class;
		}

		@Override
		public List<String> convert(Integer record) {
			return Arrays.asList(record.toString());
		}
		
	}

	@Mock
	AmazonS3Client mockS3Client;
	@Mock
	StringWithPartitionsDao mockDao;
	@Mock
	StreamResourceProvider mockStreamResourceProvider;
	@Mock
	ObjectCSVReader<Integer> mockObjectCSVReader;
	@Mock
	AmazonLogger mockAmazonLogger;
	@Mock
	ProgressCallback<Message> mockCallback;
	@Mock
	File mockFile;

	AbstractSnapshotWorker<Integer, String> worker;
	Message message;
	String messageBody;
	String[] headers = new String[]{"int"};


	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		worker = new IntToStringWorker(mockS3Client, mockDao, mockStreamResourceProvider, mockAmazonLogger);
		
		messageBody = "<Message>\n"
				+"  <bucket>dev.access.record.sagebase.org</bucket>\n"
				+"  <key>0000000001/node/2015-07-30/23-34-16-308-e4ccd5c9-8f61-4043-bbe2-df6578b4672f.csv.gz</key>\n"
				+"</Message>";
		message = new Message();
		message.setBody(messageBody);

		Mockito.when(mockStreamResourceProvider.createTempFile(Mockito.eq("prefix"), Mockito.eq("suffix"))).thenReturn(mockFile);
		Mockito.when(mockStreamResourceProvider.createObjectCSVReader(mockFile, Integer.class, headers)).thenReturn(mockObjectCSVReader);
		Mockito.when(mockDao.doesPartitionExistForTimestamp(Mockito.anyLong())).thenReturn(true);
	}

	@Test
	public void runTest() throws Exception {
		worker.run(mockCallback, message);
		Mockito.verify(mockStreamResourceProvider).createTempFile(Mockito.eq("prefix"), Mockito.eq("suffix"));
		Mockito.verify(mockS3Client).getObject((GetObjectRequest) Mockito.any(), Mockito.eq(mockFile));
		Mockito.verify(mockStreamResourceProvider).createObjectCSVReader(mockFile, Integer.class, headers);
		Mockito.verify(mockFile).delete();
		Mockito.verify(mockObjectCSVReader).close();
	}

	@Test (expected=RecoverableMessageException.class)
	public void invalidTimeTest() throws Exception {
		Mockito.when(mockDao.doesPartitionExistForTimestamp(Mockito.anyLong())).thenReturn(false);
		worker.run(mockCallback, message);
	}

	@Test
	public void deleteFileTest() throws Exception {
		Mockito.when(mockS3Client.getObject((GetObjectRequest) Mockito.any(), Mockito.eq(mockFile))).thenThrow(new AmazonClientException(""));
		try {
			worker.run(mockCallback, message);
		} catch (AmazonClientException e) {
			// expected
		}
		Mockito.verify(mockStreamResourceProvider).createTempFile(Mockito.eq("prefix"), Mockito.eq("suffix"));
		Mockito.verify(mockS3Client).getObject((GetObjectRequest) Mockito.any(), Mockito.eq(mockFile));
		Mockito.verify(mockStreamResourceProvider, Mockito.never()).createObjectCSVReader(mockFile, Integer.class, headers);
		Mockito.verify(mockFile).delete();
		Mockito.verify(mockObjectCSVReader, Mockito.never()).close();
	}
}
