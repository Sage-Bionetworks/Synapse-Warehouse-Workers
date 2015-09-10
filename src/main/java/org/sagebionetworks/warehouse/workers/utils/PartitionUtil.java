package org.sagebionetworks.warehouse.workers.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.joda.time.DateTime;

public class PartitionUtil {

	public static final String PARTITION = "PARTITION";
	public static enum Period {
		DAY,
		MONTH
	}

	private final static String PARTITION_BY_RANGE = "PARTITION BY RANGE";
	private final static String PARTITION_LESS_THAN = "PARTITION %1$s VALUES LESS THAN (%2$s)";
	private final static String PARTITION_NAME_PATTERN_FOR_DAY = "%1$S_%2$04d_%3$02d_%4$02d";
	private final static String PARTITION_NAME_PATTERN_FOR_MONTH = "%1$S_%2$04d_%3$02d";

	/**
	 * Build partitions by range on fieldName
	 * 
	 * @param tableName
	 * @param fieldName - the field that is used to partition the table. 
	 * @param period
	 * @param startDate - the lower bound date of all data stored
	 * @param endDate - the upper bound date of all data stored
	 * @return
	 */
	public static String buildPartitions(String tableName, String fieldName, Period period, DateTime startDate, DateTime endDate) {
		if (tableName == null || fieldName == null || period == null || startDate == null || endDate == null || endDate.isBefore(startDate.getMillis())) {
			throw new IllegalArgumentException();
		}
		String partition = PARTITION_BY_RANGE + " (" + fieldName + ") (\n";
		Map<String, Long> partitions = getPartitions(tableName, period, startDate, endDate);
		Iterator<String> it = partitions.keySet().iterator();
		while (it.hasNext()) {
			String partitionName = it.next();
			partition += String.format(PARTITION_LESS_THAN, partitionName, partitions.get(partitionName));
			if (it.hasNext()) {
				partition += ",\n";
			} else {
				partition += "\n";
			}
		}
		partition += ");\n";
		return partition;
	}

	/**
	 * 
	 * @param tableName
	 * @param period
	 * @param startDate
	 * @param endDate
	 * @return a map of partitionName to value for table tableName from startDate to
	 *         endDate with period
	 */
	public static Map<String, Long> getPartitions(String tableName, Period period, DateTime startDate, DateTime endDate) {
		if (tableName == null || period == null || startDate == null || endDate == null || endDate.isBefore(startDate.getMillis())) {
			throw new IllegalArgumentException();
		}
		// adding an extra day/month to endDate
		switch (period) {
			case DAY:
				endDate = endDate.plusDays(1);
				break;
			case MONTH:
				endDate = endDate.plusMonths(1);
		}
		startDate = floorDateByPeriod(startDate, period);
		endDate = floorDateByPeriod(endDate, period);
		SortedMap<String, Long> partitions = new TreeMap<String, Long>();
		DateTime nextDate = startDate;
		while (nextDate.isBefore(endDate.getMillis())) {
			String partitionName = getPartitionName(tableName, nextDate, period);
			partitions.put(partitionName, nextDate.getMillis());
			switch (period) {
				case DAY:
					nextDate = nextDate.plusDays(1);
					break;
				case MONTH:
					nextDate = nextDate.plusMonths(1);
			}
		}
		String partitionName = getPartitionName(tableName, nextDate, period);
		partitions.put(partitionName, nextDate.getMillis());
		return partitions;
	}

	/**
	 * 
	 * @param tableName
	 * @param date
	 * @param period
	 * @return
	 */
	public static String getPartitionName(String tableName, DateTime date, Period period) {
		if (period == null || date == null || tableName == null) throw new IllegalArgumentException();
		switch (period) {
			case DAY: return String.format(PARTITION_NAME_PATTERN_FOR_DAY, tableName, date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
			case MONTH: return String.format(PARTITION_NAME_PATTERN_FOR_MONTH, tableName, date.getYear(), date.getMonthOfYear());
		}
		return null;
	}

	/**
	 * 
	 * @param date
	 * @param period
	 * @return
	 */
	public static DateTime floorDateByPeriod(DateTime date, Period period) {
		if (period == null || date == null) throw new IllegalArgumentException();
		switch (period) {
			case DAY: return new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 0, 0);
			case MONTH: return new DateTime(date.getYear(), date.getMonthOfYear(), 1, 0, 0);
		}
		return null;
	}
}
