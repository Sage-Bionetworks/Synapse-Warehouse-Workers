package org.sagebionetworks.warehouse.workers.log;

import java.util.Date;

import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.model.LogRecord;
import org.sagebionetworks.warehouse.workers.utils.LogRecordUtils;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.google.inject.Inject;

public class CloudWatchLoggerImpl implements CloudWatchLogger{
	public static final String DIMENSION_NAME = "Class";
	public static final String NAMESPACE_CONFIG_KEY = "org.sagebionetworks.warehouse.worker.cloudwatch.namespace";
	
	AmazonCloudWatchClient cloudWatchClient;
	String namespace;
	
	@Inject
	public CloudWatchLoggerImpl(AmazonCloudWatchClient cloudWatchClient, Configuration config) {
		this.cloudWatchClient = cloudWatchClient;
		this.namespace = config.getProperty(NAMESPACE_CONFIG_KEY);
	}

	@Override
	public <T> void log(ProgressCallback<T> progressCallback, T toCallback, LogRecord toLog) {
		if (!LogRecordUtils.isValidLogRecord(toLog)) {
			return;
		}
		Dimension dimension = new Dimension()
				.withName(DIMENSION_NAME)
				.withValue(toLog.getClassName());

		MetricDatum datum = new MetricDatum()
				.withMetricName(toLog.getExceptionName())
				.withTimestamp(new Date(toLog.getTimestamp()))
				.withUnit(StandardUnit.Count)
				.withValue(1.0)
				.withDimensions(dimension);

		PutMetricDataRequest request = new PutMetricDataRequest()
				.withNamespace(namespace)
				.withMetricData(datum);

		if (progressCallback != null) {
			progressCallback.progressMade(toCallback);
		}
		cloudWatchClient.putMetricData(request);
	}

}
