package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;

public class PartitionUtilTest {

	@Test (expected=IllegalArgumentException.class)
	public void partitionWithEndDateBeforeStartDate() {
		DateTime endDate = new DateTime();
		DateTime startDate = endDate.plusDays(1);
		PartitionUtil.buildPartitions("TEST_TABLE", "timestamp", Period.DAY, startDate, endDate);
	}

	@Test
	public void partitionWithDayBetweenTwoYears() {
		DateTime startDate = new DateTime(2015, 12, 30, 0, 0);
		DateTime endDate = new DateTime(2016, 1, 2, 0, 0);
		String partition = PartitionUtil.buildPartitions("TEST_TABLE", "timestamp", Period.DAY, startDate, endDate);
		String expected = "PARTITION BY RANGE (timestamp) (\n"
				+ "PARTITION TEST_TABLE_2015_12_30 VALUES LESS THAN (1451462400000),\n"
				+ "PARTITION TEST_TABLE_2015_12_31 VALUES LESS THAN (1451548800000),\n"
				+ "PARTITION TEST_TABLE_2016_1_1 VALUES LESS THAN (1451635200000),\n"
				+ "PARTITION TEST_TABLE_2016_1_2 VALUES LESS THAN (1451721600000)\n"
				+ ");\n";
		assertEquals(expected, partition);
	}

	@Test
	public void partitionWithMonthBetweenTwoYears() {
		DateTime startDate = new DateTime(2015, 11, 30, 0, 0);
		DateTime endDate = new DateTime(2016, 2, 2, 0, 0);
		String partition = PartitionUtil.buildPartitions("TEST_TABLE", "timestamp", Period.MONTH, startDate, endDate);
		String expected = "PARTITION BY RANGE (timestamp) (\n"
				+ "PARTITION TEST_TABLE_2015_11_30 VALUES LESS THAN (1448870400000),\n"
				+ "PARTITION TEST_TABLE_2015_12_30 VALUES LESS THAN (1451462400000),\n"
				+ "PARTITION TEST_TABLE_2016_1_30 VALUES LESS THAN (1454140800000),\n"
				+ "PARTITION TEST_TABLE_2016_2_29 VALUES LESS THAN (1456732800000)\n"
				+ ");\n";
		assertEquals(expected, partition);
	}

	@Test
	public void partitionWithStartDateEqualsEndDate() {
		DateTime startDate = new DateTime(2015, 11, 30, 0, 0);
		DateTime endDate = startDate;
		String partition = PartitionUtil.buildPartitions("TEST_TABLE", "timestamp", Period.MONTH, startDate, endDate);
		String expected = "PARTITION BY RANGE (timestamp) (\n"
				+ "PARTITION TEST_TABLE_2015_11_30 VALUES LESS THAN (1448870400000)\n"
				+ ");\n";
		assertEquals(expected, partition);
	}

	@Test
	public void partitionWithDaysOverTenYears() {
		DateTime startDate = new DateTime(2015, 1, 1, 0, 0, 0, 0);
		DateTime endDate = new DateTime(2025, 1, 1, 0, 0, 0, 0);
		String partition = PartitionUtil.buildPartitions("TEST_TABLE", "timestamp", Period.DAY, startDate, endDate);
		assertTrue(partition.contains("TEST_TABLE_2025_1_1"));
		assertFalse(partition.contains("TEST_TABLE_2025_1_2"));
	}
}
