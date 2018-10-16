package org.sagebionetworks.warehouse.workers.utils;

import java.util.Date;

import org.joda.time.DateTime;

public class DateTimeUtils {
	public static final String DATE_FORMAT = "%04d-%02d-%02d";

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

	/**
	 * 
	 * @param date
	 * @return a string representation of date in format yyyy-mm-dd
	 */
	public static String toDateString(Date date) {
		DateTime time = new DateTime(date);
		return toDateString(time);
	}

	/**
	 * 
	 * @param time
	 * @return
	 */
	public static String toDateString(DateTime time) {
		return String.format(DATE_FORMAT, time.getYear(), time.getMonthOfYear(), time.getDayOfMonth());
	}

	/**
	 * 
	 * @param time
	 * @return the first day of the previous month
	 */
	public static DateTime getFirstDayOfPreviousMonth(DateTime time) {
		DateTime prevMonth = time.minusMonths(1)
				.withDayOfMonth(1)
				.withHourOfDay(0)
				.withMinuteOfHour(0)
				.withSecondOfMinute(0)
				.withMillisOfSecond(0);
		return prevMonth;
	}

}
