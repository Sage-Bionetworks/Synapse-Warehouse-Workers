package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;

public class PartitionUtilTest {

	/*
	 * buildPartitions() tests
	 */
	@Test
	public void buildPartitionsWithDayBetweenTwoYears() {
		DateTime startDate = new DateTime(2015, 12, 30, 0, 0, DateTimeZone.UTC);
		DateTime endDate = new DateTime(2016, 1, 1, 0, 0, DateTimeZone.UTC);
		String partition = PartitionUtil.buildPartitions("TEST_TABLE", "timestamp", Period.DAY, startDate, endDate);
		String expected = "PARTITION BY RANGE (timestamp) (\n"
				+ "PARTITION TEST_TABLE_2015_12_30 VALUES LESS THAN (1451433600000),\n"
				+ "PARTITION TEST_TABLE_2015_12_31 VALUES LESS THAN (1451520000000),\n"
				+ "PARTITION TEST_TABLE_2016_01_01 VALUES LESS THAN (1451606400000),\n"
				+ "PARTITION TEST_TABLE_2016_01_02 VALUES LESS THAN (1451692800000)\n"
				+ ");\n";
		assertEquals(expected, partition);
	}

	/*
	 * getPartitions() tests
	 */
	@Test (expected=IllegalArgumentException.class)
	public void getPartitionsWithEndDateBeforeStartDate() {
		DateTime endDate = new DateTime();
		DateTime startDate = endDate.plusDays(1);
		PartitionUtil.getPartitions("TEST_TABLE", Period.DAY, startDate, endDate);
	}

	@Test
	public void getPartitionsWithDayBetweenTwoYears() {
		DateTime startDate = new DateTime(2015, 12, 30, 0, 0, DateTimeZone.UTC);
		DateTime endDate = new DateTime(2016, 1, 1, 0, 0, DateTimeZone.UTC);
		SortedMap<String, Long> expected = new TreeMap<String, Long>();
		expected.put("TEST_TABLE_2015_12_30", 1451433600000L);
		expected.put("TEST_TABLE_2015_12_31", 1451520000000L);
		expected.put("TEST_TABLE_2016_01_01", 1451606400000L);
		expected.put("TEST_TABLE_2016_01_02", 1451692800000L);
		assertEquals(expected, PartitionUtil.getPartitions("TEST_TABLE", Period.DAY, startDate, endDate));
	}

	@Test
	public void getPartitionsWithMonthBetweenTwoYears() {
		DateTime startDate = new DateTime(2015, 11, 30, 0, 0, DateTimeZone.UTC);
		DateTime endDate = new DateTime(2016, 1, 2, 0, 0, DateTimeZone.UTC);
		SortedMap<String, Long> expected = new TreeMap<String, Long>();
		expected.put("TEST_TABLE_2015_11", 1446336000000L);
		expected.put("TEST_TABLE_2015_12", 1448928000000L);
		expected.put("TEST_TABLE_2016_01", 1451606400000L);
		expected.put("TEST_TABLE_2016_02", 1454284800000L);
		assertEquals(expected, PartitionUtil.getPartitions("TEST_TABLE", Period.MONTH, startDate, endDate));
	}

	@Test
	public void getPartitionsWithStartDateEqualsEndDate() {
		DateTime startDate = new DateTime(2015, 11, 30, 0, 0, DateTimeZone.UTC);
		DateTime endDate = startDate;
		SortedMap<String, Long> expected = new TreeMap<String, Long>();
		expected.put("TEST_TABLE_2015_11", 1446336000000L);
		expected.put("TEST_TABLE_2015_12", 1448928000000L);
		assertEquals(expected, PartitionUtil.getPartitions("TEST_TABLE", Period.MONTH, startDate, endDate));
	}

	@Test
	public void getPartitionsWithStartDateAndEndDateInTheSameMonth() {
		DateTime startDate = new DateTime(2015, 11, 3, 0, 0, DateTimeZone.UTC);
		DateTime endDate = new DateTime(2015, 11, 30, 0, 0, DateTimeZone.UTC);
		SortedMap<String, Long> expected = new TreeMap<String, Long>();
		expected.put("TEST_TABLE_2015_11", 1446336000000L);
		expected.put("TEST_TABLE_2015_12", 1448928000000L);
		assertEquals(expected, PartitionUtil.getPartitions("TEST_TABLE", Period.MONTH, startDate, endDate));
	}

	@Test
	public void getPartitionsWithDaysOverTenYears() {
		DateTime startDate = new DateTime(2015, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
		DateTime endDate = new DateTime(2025, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
		Map<String, Long> actual = PartitionUtil.getPartitions("TEST_TABLE", Period.DAY, startDate, endDate);
		assertTrue(actual.containsKey("TEST_TABLE_2025_01_01"));
		assertTrue(actual.containsKey("TEST_TABLE_2025_01_02"));
		assertFalse(actual.containsKey("TEST_TABLE_2025_01_03"));
	}

	/*
	 * getPartitionName() tests
	 */
	@Test
	public void getPartitionNameForDayPeriod() {
		DateTime date = new DateTime(2015, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
		String partitionName = "TEST_TABLE_2015_01_01";
		assertEquals(partitionName, PartitionUtil.getPartitionName("TEST_TABLE", date, Period.DAY));
	}

	@Test
	public void getPartitionNameForMonthPeriod() {
		DateTime date = new DateTime(2015, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
		String partitionName = "TEST_TABLE_2015_01";
		assertEquals(partitionName, PartitionUtil.getPartitionName("TEST_TABLE", date, Period.MONTH));
	}

	/*
	 * floorDateByPeriod() tests
	 */
	@Test
	public void floorDateByDay() {
		DateTime date = new DateTime(2015, 1, 4, 21, 34, DateTimeZone.UTC);
		assertEquals(new DateTime(2015, 1, 4, 0, 0, DateTimeZone.UTC), PartitionUtil.floorDateByPeriod(date, Period.DAY));
	}

	@Test
	public void floorDateByMonth() {
		DateTime date = new DateTime(2015, 1, 4, 21, 34, DateTimeZone.UTC);
		assertEquals(new DateTime(2015, 1, 1, 0, 0, DateTimeZone.UTC), PartitionUtil.floorDateByPeriod(date, Period.MONTH));
	}
}
