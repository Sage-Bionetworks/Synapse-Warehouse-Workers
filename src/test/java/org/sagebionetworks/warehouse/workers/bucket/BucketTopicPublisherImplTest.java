package org.sagebionetworks.warehouse.workers.bucket;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.amazonaws.services.sns.AmazonSNSClient;

public class BucketTopicPublisherImplTest {

	private AmazonSNSClient mockAmazonSNSClient;
	private TopicDaoProvider mockTopicDaoProvider;
	private BucketTopicPublisherImpl publisher;

	@Before
	public void before() {
		mockAmazonSNSClient = Mockito.mock(AmazonSNSClient.class);
		mockTopicDaoProvider = Mockito.mock(TopicDaoProvider.class);
		publisher = new BucketTopicPublisherImpl(mockAmazonSNSClient, mockTopicDaoProvider);
	}

	@Test
	public void test() {
		String bucket = "dev.snapshot.record.sagebase.org";
		String key ="0000000001/node/2015-07-30/23-34-16-308-e4ccd5c9-8f61-4043-bbe2-df6578b4672f.csv.gz";

		String topicArn = "nodeTypeTopicArn";
		Mockito.when(mockTopicDaoProvider.getTopicArn("node")).thenReturn(topicArn);
		String message = "<Message>\n"
				+"  <bucket>dev.snapshot.record.sagebase.org</bucket>\n"
				+"  <key>0000000001/node/2015-07-30/23-34-16-308-e4ccd5c9-8f61-4043-bbe2-df6578b4672f.csv.gz</key>\n"
				+"</Message>";

		ArgumentCaptor<String> topicArnCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

		publisher.publishS3ObjectToTopic(bucket, key);
		Mockito.verify(mockAmazonSNSClient).publish(topicArnCaptor.capture(), messageCaptor.capture());
		assertEquals(topicArn, topicArnCaptor.getValue());
		assertEquals(message, messageCaptor.getValue());
	}

}
