package org.sagebionetworks.warehouse.workers.log;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.sagebionetworks.warehouse.workers.log.CloudWatchLoggerImpl.DIMENSION_NAME;
import static org.sagebionetworks.warehouse.workers.log.CloudWatchLoggerImpl.NAMESPACE_CONFIG_KEY;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.model.LogRecord;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;

public class CloudWatchLoggerImplTest {
	@Mock
	AmazonCloudWatchClient mockCloudWatchClient;
	@Mock
	Configuration mockConfig;
	@Mock
	ProgressCallback<Void> mockProgressCallback;
	
	CloudWatchLoggerImpl logger;
	String namespace = "test.org.sagebionetworks.warehouse.workers";
	ArgumentCaptor<PutMetricDataRequest> captor;
	
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		when(mockConfig.getProperty(NAMESPACE_CONFIG_KEY)).thenReturn(namespace);
		logger = new CloudWatchLoggerImpl(mockCloudWatchClient, mockConfig);
		captor = ArgumentCaptor.forClass(PutMetricDataRequest.class);
	}
	
	@Test
	public void testConstructor() {
		verify(mockConfig).getProperty(NAMESPACE_CONFIG_KEY);
	}

	@Test
	public void testLog() {
		long timestamp = System.currentTimeMillis();
		LogRecord toLog = new LogRecord(timestamp, "workerName", "exceptionName", "trace");
		logger.log(mockProgressCallback, toLog);
		verify(mockProgressCallback).progressMade(null);
		verify(mockCloudWatchClient).putMetricData(captor.capture());
		PutMetricDataRequest captured = captor.getValue();
		assertNotNull(captured);
		assertEquals(namespace, captured.getNamespace());
		List<MetricDatum> data = captured.getMetricData();
		assertNotNull(data);
		assertEquals(1, data.size());
		MetricDatum datum = data.get(0);
		assertNotNull(datum);
		assertEquals(toLog.getExceptionName(), datum.getMetricName());
		assertEquals(new Date(toLog.getTimestamp()), datum.getTimestamp());
		assertEquals("Count", datum.getUnit());
		assertEquals(Double.valueOf(1.0), datum.getValue());
		List<Dimension> dimensions = datum.getDimensions();
		assertNotNull(dimensions);
		assertEquals(1, dimensions.size());
		Dimension dimension = dimensions.get(0);
		assertNotNull(dimension);
		assertEquals(DIMENSION_NAME, dimension.getName());
		assertEquals(toLog.getClassName(), dimension.getValue());
	}

}
