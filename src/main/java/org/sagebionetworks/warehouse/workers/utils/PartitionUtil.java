package org.sagebionetworks.warehouse.workers.utils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
	public static Map<String, Long> getPartitionsForPeriod(String tableName, Period period, DateTime startDate, DateTime endDate) {
		SortedMap<String, Long> partitions = new TreeMap<String, Long>(partitionNameComparator);
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

	/**
	 * Since partitions must be created in strictly increasing order, use this comparator
	 * to sort the key set.
	 */
	private final static Comparator<String> partitionNameComparator = new Comparator<String>() {

		@Override
		public int compare(String partitionName1, String partitionName2) {
			String[] name1parts = partitionName1.split("_");
			String[] name2parts = partitionName2.split("_");
			int length = name1parts.length;
			int year1 = Integer.parseInt(name1parts[length - 3]);
			int year2 = Integer.parseInt(name2parts[length - 3]);
			if (year1 != year2) return year1 - year2;
			int month1 = Integer.parseInt(name1parts[length - 2]);
			int month2 = Integer.parseInt(name2parts[length - 2]);
			if (month1 != month2) return month1 - month2;
			int day1 = Integer.parseInt(name1parts[length - 1]);
			int day2 = Integer.parseInt(name2parts[length - 1]);
			return day1 - day2;
		}
	};
}
