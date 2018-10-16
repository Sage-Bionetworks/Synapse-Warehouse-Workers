package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.warehouse.workers.model.LogRecord;

public class LogRecordTestUtils {

	public static LogRecord createValidLogRecord() {
		return new LogRecord(System.currentTimeMillis(), "workerName", "exceptionName", "trace");
	}
}
