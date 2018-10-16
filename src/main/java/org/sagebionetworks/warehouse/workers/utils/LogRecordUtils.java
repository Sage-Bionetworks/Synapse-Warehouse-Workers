package org.sagebionetworks.warehouse.workers.utils;

import java.util.UUID;

import org.joda.time.DateTime;
import org.sagebionetworks.warehouse.workers.model.LogRecord;

public class LogRecordUtils {

	public static Boolean isValidLogRecord(LogRecord record) {
		if (record 						== null) return false;
		if (record.getWorkerName() 		== null) return false;
		if (record.getExceptionName() 	== null) return false;
		if (record.getStacktrace() 		== null) return false;
		return true;
	}

	public static String[] getFormatedLog(LogRecord toLog) {
		String[] logData = new String[2];
		String dateString = DateTimeUtils.toDateString(new DateTime(toLog.getTimestamp()));
		logData[0] = String.format("%d::%s::%s::%s", toLog.getTimestamp(), dateString, toLog.getWorkerName(), toLog.getExceptionName());
		logData[1] = toLog.getStacktrace();
		return logData;
	}

	public static String getKey(LogRecord toLog) {
		DateTime date = new DateTime(toLog.getTimestamp());
		String dateString = DateTimeUtils.toDateString(date);
		String key = String.format("%02d-%02d-%02d-%03d-%s", date.getHourOfDay(), date.getMinuteOfHour(), date.getSecondOfMinute(), date.getMillisOfSecond(), UUID.randomUUID());
		return String.format("%s/%s/%s.log.gz", dateString, toLog.getWorkerName(), key);
	}
}
