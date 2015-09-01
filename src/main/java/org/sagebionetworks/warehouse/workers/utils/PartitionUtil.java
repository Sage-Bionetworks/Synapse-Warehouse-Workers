package org.sagebionetworks.warehouse.workers.utils;

import org.joda.time.DateTime;

public class PartitionUtil {

	public static final String PARTITION = "PARTITION";
	public static enum Period {
		DAY,
		WEEK,
		MONTH
	}

	private final static String PARTITION_BY_RANGE = "PARTITION BY RANGE";
	private final static String PARTITION_LESS_THAN = "PARTITION %1$s VALUES LESS THAN (%2$s)";
	private final static String PARTITION_PREFIX = "_P";

	/**
	 * Build a partition by range on fieldName
	 * 
	 * @param tableName
	 * @param fieldName - the field that is used to partition the table. 
	 * @param period - 
	 * @param startDate - the lower bound date of all data stored
	 * @param endDate - the upper bound date of all data stored
	 * @return
	 */
	public static String buildPartition(String tableName, String fieldName, Period period, DateTime startDate, DateTime endDate) {
		if (endDate.isBefore(startDate.getMillis())) {
			throw new IllegalArgumentException();
		}
		String partition = PARTITION_BY_RANGE + " (" + fieldName + ") (\n";
		int partitionNo = 0;
		DateTime nextDate = startDate;
		while (nextDate.isBefore(endDate.getMillis())) {
			partition += String.format(PARTITION_LESS_THAN, tableName + PARTITION_PREFIX + partitionNo, nextDate.getMillis()) + ",\n";
			partitionNo++;
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
		partition += String.format(PARTITION_LESS_THAN, tableName + PARTITION_PREFIX + partitionNo, endDate.getMillis()) + "\n";
		partition += ");\n";
		return partition;
	}
}
