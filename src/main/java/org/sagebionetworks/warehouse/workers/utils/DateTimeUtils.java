package org.sagebionetworks.warehouse.workers.utils;

import java.util.Date;

import org.joda.time.DateTime;

public class DateTimeUtils {

	/**
	 * This method takes a java.util.Date and return the next month java.util.Date
	 * 
	 * @param month
	 * @return
	 */
	public static Date getNextMonth(Date month) {
		DateTime time = new DateTime(month).plusMonths(1);
		return time.toDate();
	}
}
