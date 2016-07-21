package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class DateTimeUtilsTest {

	@Test
	public void testGetNextMonthSameYear(){
		Date date = new Date(2016, 1, 1);
		assertEquals(new Date(2016, 2, 1), DateTimeUtils.getNextMonth(date));
	}

	@Test
	public void testGetNextMonthDifferentYear(){
		Date date = new Date(2015, 12, 1);
		assertEquals(new Date(2016, 1, 1), DateTimeUtils.getNextMonth(date));
	}
}
