package org.sagebionetworks.warehouse.workers.utils;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

public class PartitionUtil {

	public static final String PARTITION = "PARTITION";
	public static enum Period {
		DAY,
		WEEK,
		MONTH
	}

	public final static String PARTITION_BY_RANGE = "PARTITION BY RANGE";
	public final static String PARTITION_LESS_THAN = "PARTITION %1$s VALUES LESS THAN (%2$s)";
	public final static String PARTITION_NAME_PATTERN = "%1$S_%2$d_%3$d_%4$d";

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
		if (endDate.isBefore(startDate.getMillis())) {
			throw new IllegalArgumentException();
		}
		String partition = PARTITION_BY_RANGE + " (" + fieldName + ") (\n";
		Map<String, Long> partitions = getPartitionsForPeriod(tableName, period, startDate, endDate);
		for (String partitionName : partitions.keySet()) {
			partition += String.format(PARTITION_LESS_THAN, partitionName, partitions.get(partitionName)) + "\n";
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
	public static Map<String, Long> getPartitionsForPeriod(String tableName, Period period, DateTime startDate, DateTime endDate) {
		Map<String, Long> partitions = new HashMap<String, Long>();
		DateTime nextDate = startDate;
		while (nextDate.isBefore(endDate.getMillis())) {
			String partitionName = String.format(PARTITION_NAME_PATTERN, tableName, nextDate.getYear(), nextDate.getMonthOfYear(), nextDate.getDayOfMonth());
			partitions.put(partitionName, nextDate.getMillis());
			switch (period) {
				case DAY:
					nextDate = nextDate.plusDays(1);
					break;
				case WEEK:
					nextDate = nextDate.plusWeeks(1);
					break;
				case MONTH:
					nextDate = nextDate.plusMonths(1);
			}
		}
		String partitionName = String.format(PARTITION_NAME_PATTERN, tableName, nextDate.getYear(), nextDate.getMonthOfYear(), nextDate.getDayOfMonth());
		partitions.put(partitionName, nextDate.getMillis());
		return partitions;
	}
}
