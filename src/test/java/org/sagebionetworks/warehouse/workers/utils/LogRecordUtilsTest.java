package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.warehouse.workers.model.LogRecord;

public class LogRecordUtilsTest {

	@Test
	public void testValidLogRecord() {
		assertTrue(LogRecordUtils.isValidLogRecord(LogRecordTestUtils.createValidLogRecord()));
	}
	
	@Test
	public void testInvalidLogRecordWithNull() {
		assertFalse(LogRecordUtils.isValidLogRecord(null));
	}
	
	@Test
	public void testInvalidLogRecordWithNullWorkerName() {
		LogRecord record = LogRecordTestUtils.createValidLogRecord();
		record.setClassName(null);
		assertFalse(LogRecordUtils.isValidLogRecord(record));
	}
	
	@Test
	public void testInvalidLogRecordWithNullExceptionName() {
		LogRecord record = LogRecordTestUtils.createValidLogRecord();
		record.setExceptionName(null);
		assertFalse(LogRecordUtils.isValidLogRecord(record));
	}

	@Test
	public void testInvalidLogRecordWithNullStacktrace() {
		LogRecord record = LogRecordTestUtils.createValidLogRecord();
		record.setStacktrace(null);
		assertFalse(LogRecordUtils.isValidLogRecord(record));
	}

	@Test (expected = IllegalArgumentException.class)
	public void testGetFormattedLogWithInvalidRecord() {
		LogRecordUtils.getFormattedLog(null);
	}
	
	@Test
	public void testGetFormattedLog() {
		LogRecord record = LogRecordTestUtils.createValidLogRecord();
		record.setTimestamp(1539717007681L); // Tuesday, October 16, 2018 7:10:07.681 PM (UTC)
		String[] formatted = LogRecordUtils.getFormattedLog(record);
		assertEquals(2, formatted.length);
		assertEquals("1539717007681::"+record.getClassName()+"::"+record.getExceptionName(),formatted[0]);
		assertEquals(record.getStacktrace(), formatted[1]);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetKeyWithInvalidRecord() {
		LogRecordUtils.getFormattedLog(null);
	}
	
	@Test
	public void testGetKey() {
		LogRecord record = LogRecordTestUtils.createValidLogRecord();
		record.setTimestamp(1539717007681L); // Tuesday, October 16, 2018 7:10:07.681 PM (UTC)
		String key = LogRecordUtils.getKey(record);
		System.out.println(key);
		assertTrue(key.startsWith("2018-10-16/"+record.getClassName()+"/19-10-07-681-"));
		assertTrue(key.endsWith(".log.gz"));
	}
}
