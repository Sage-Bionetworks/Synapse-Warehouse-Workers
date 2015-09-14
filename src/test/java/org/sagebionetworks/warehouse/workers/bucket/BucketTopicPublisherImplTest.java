package org.sagebionetworks.warehouse.workers.bucket;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sagebionetworks.warehouse.workers.config.Configuration;

import com.amazonaws.services.sns.AmazonSNSClient;

public class BucketTopicPublisherImplTest {

	private AmazonSNSClient mockAmazonSNSClient;
	private TopicDaoProvider mockTopicDaoProvider;
	private BucketTopicPublisherImpl publisher;
	private Configuration mockConfig;
	private String bucket;
	private String key;
	private String topicArn;
	private String message;

	@Before
	public void before() {
		mockAmazonSNSClient = Mockito.mock(AmazonSNSClient.class);
		mockTopicDaoProvider = Mockito.mock(TopicDaoProvider.class);
		mockConfig = Mockito.mock(Configuration.class);
		publisher = new BucketTopicPublisherImpl(mockAmazonSNSClient, mockTopicDaoProvider, mockConfig);
		bucket = "dev.snapshot.record.sagebase.org";
		key ="0000000001/node/2015-07-30/23-34-16-308-e4ccd5c9-8f61-4043-bbe2-df6578b4672f.csv.gz";

		topicArn = "nodeTypeTopicArn";
		Mockito.when(mockTopicDaoProvider.getTopicArn("node")).thenReturn(topicArn);
		message = "<Message>\n"
				+"  <bucket>dev.snapshot.record.sagebase.org</bucket>\n"
				+"  <key>0000000001/node/2015-07-30/23-34-16-308-e4ccd5c9-8f61-4043-bbe2-df6578b4672f.csv.gz</key>\n"
				+"</Message>";
	}

	@Test
	public void validTimeTest() {
		Mockito.when(mockConfig.getStartDate()).thenReturn(new DateTime(2000, 1, 1, 0, 0));
		Mockito.when(mockConfig.getEndDate()).thenReturn(new DateTime(3000, 1, 1, 0, 0));

		ArgumentCaptor<String> topicArnCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

		publisher.publishS3ObjectToTopic(bucket, key);
		Mockito.verify(mockAmazonSNSClient).publish(topicArnCaptor.capture(), messageCaptor.capture());
		assertEquals(topicArn, topicArnCaptor.getValue());
		assertEquals(message, messageCaptor.getValue());
	}

	@Test
	public void invalidTimeTest() {
		Mockito.when(mockConfig.getStartDate()).thenReturn(new DateTime(1, 1, 1, 0, 0));
		Mockito.when(mockConfig.getEndDate()).thenReturn(new DateTime(2, 1, 1, 0, 0));
		publisher.publishS3ObjectToTopic(bucket, key);
		Mockito.verify(mockAmazonSNSClient, Mockito.never()).publish(Mockito.anyString(), Mockito.anyString());
	}
}
