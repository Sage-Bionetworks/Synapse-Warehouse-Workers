package org.sagebionetworks.warehouse.workers.log;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.aws.utils.s3.BucketDao;
import org.sagebionetworks.warehouse.workers.BucketDaoProvider;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.TestContext;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.amazonaws.services.s3.AmazonS3Client;

public class AmazonLoggerIntegrationTest {
	
	private static long MAX_WAIT_MS = 2*60*1000;
	
	AmazonS3Client s3Client = TestContext.singleton().getInstance(AmazonS3Client.class);
	AmazonCloudWatchClient cloudWatchClient = TestContext.singleton().getInstance(AmazonCloudWatchClient.class);
	AmazonLogger amazonLogger = TestContext.singleton().getInstance(AmazonLogger.class);
	Configuration config = TestContext.singleton().getInstance(Configuration.class);
	BucketDaoProvider bucketDaoProvider = TestContext.singleton().getInstance(BucketDaoProvider.class);
	BucketDao bucketDao;
	
	@Before
	public void before(){
		String bucketName = config.getProperty(S3LoggerImpl.BUCKET_CONFIG_KEY);
		bucketDao = bucketDaoProvider.createBucketDao(bucketName);
		bucketDao.deleteAllObjectsWithPrefix(null);
	}
	
	@Test
	public void testLog() throws Exception {
		Long start = System.currentTimeMillis();
		String metricName = "testLog";
		Dimension dimension = new Dimension()
				.withName(CloudWatchLoggerImpl.DIMENSION_NAME)
				.withValue(this.getClass().getSimpleName());
		GetMetricStatisticsRequest getMetricStatisticsRequest = new GetMetricStatisticsRequest()
				.withNamespace(config.getProperty(CloudWatchLoggerImpl.NAMESPACE_CONFIG_KEY))
				.withDimensions(dimension)
				.withMetricName(metricName)
				.withStatistics(Statistic.SampleCount)
				.withStartTime(new Date(start))
				.withEndTime(new Date(System.currentTimeMillis()))
				.withPeriod(60); // 60 seconds as minimum requirements
		GetMetricStatisticsResult initialStats = cloudWatchClient.getMetricStatistics(getMetricStatisticsRequest);
		System.out.println(initialStats);
		amazonLogger.logNonRetryableError(null, null, this.getClass().getSimpleName(), metricName, "some string");
		
		// wait for the file to exist
		waitForS3Key();
		// wait for 60 seconds
		Thread.sleep(60*1000);
		// check cloudwatch
		getMetricStatisticsRequest.withEndTime(new Date(System.currentTimeMillis()));
		GetMetricStatisticsResult currentStats = cloudWatchClient.getMetricStatistics(getMetricStatisticsRequest);
		System.out.println(currentStats);
		assertTrue(currentStats.getDatapoints().size() > initialStats.getDatapoints().size());
	}

	private void waitForS3Key() throws InterruptedException {
		long start = System.currentTimeMillis();
		while(true){
			Iterator<String> i = bucketDao.keyIterator(null);
			while (i.hasNext()) {
				return;
			}
			assertTrue((System.currentTimeMillis() - start) < MAX_WAIT_MS);
			Thread.sleep(2000);
		}
	}
}
