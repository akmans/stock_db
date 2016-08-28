package com.akmans.trade.core.utils;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Locale;

import org.junit.Test;

public class DateUtilTest {
	@Test
	public void testGetFirstDayOfWeekAndGetLastDayOfWeek() {
		Calendar cal = Calendar.getInstance(Locale.JAPAN);
		// Monday (target day)
		cal.clear();
		cal.set(2016, 0, 17);
		long targetFirstDay = cal.getTime().getTime();
		cal.set(2016, 0, 23);
		long targetLastDay = cal.getTime().getTime();

		// Test Monday
		assertTrue(targetFirstDay - DateUtil.getFirstDayOfWeek(cal.getTime()).getTime() == 0);
		assertTrue(targetLastDay - DateUtil.getLastDayOfWeek(cal.getTime()).getTime() == 0);
		// Test Tuesday
		cal.set(2016, 0, 18);
		assertTrue(targetFirstDay - DateUtil.getFirstDayOfWeek(cal.getTime()).getTime() == 0);
		assertTrue(targetLastDay - DateUtil.getLastDayOfWeek(cal.getTime()).getTime() == 0);
		// Test Wednesday
		cal.set(2016, 0, 19);
		assertTrue(targetFirstDay - DateUtil.getFirstDayOfWeek(cal.getTime()).getTime() == 0);
		assertTrue(targetLastDay - DateUtil.getLastDayOfWeek(cal.getTime()).getTime() == 0);
		// Test Thursday
		cal.set(2016, 0, 20);
		assertTrue(targetFirstDay - DateUtil.getFirstDayOfWeek(cal.getTime()).getTime() == 0);
		assertTrue(targetLastDay - DateUtil.getLastDayOfWeek(cal.getTime()).getTime() == 0);
		// Test Friday
		cal.set(2016, 0, 21);
		assertTrue(targetFirstDay - DateUtil.getFirstDayOfWeek(cal.getTime()).getTime() == 0);
		assertTrue(targetLastDay - DateUtil.getLastDayOfWeek(cal.getTime()).getTime() == 0);
		// Test Saturday
		cal.set(2016, 0, 22);
		assertTrue(targetFirstDay - DateUtil.getFirstDayOfWeek(cal.getTime()).getTime() == 0);
		assertTrue(targetLastDay - DateUtil.getLastDayOfWeek(cal.getTime()).getTime() == 0);
		// Test Sunday
		cal.set(2016, 0, 23);
		assertTrue(targetFirstDay - DateUtil.getFirstDayOfWeek(cal.getTime()).getTime() == 0);
		assertTrue(targetLastDay - DateUtil.getLastDayOfWeek(cal.getTime()).getTime() == 0);
	}

	@Test
	public void testGetFirstDayOfMonthAndGetLastDayOfMonth() {
		Calendar cal = Calendar.getInstance(Locale.JAPAN);
		// January
		cal.clear();
		cal.set(2016, 0, 1);
		Calendar target = Calendar.getInstance(Locale.JAPAN);;
		target.clear();
		// Test January
		target.set(2016, 0, 1);
		assertEquals(target.getTime(), DateUtil.getFirstDayOfMonth(cal.getTime()));
		target.set(2016, 0, 31);
		assertEquals(target.getTime(), DateUtil.getLastDayOfMonth(cal.getTime()));
		// Test February
		cal.set(2016, 1, 2);
		target.set(2016, 1, 1);
		assertEquals(target.getTime(), DateUtil.getFirstDayOfMonth(cal.getTime()));
		target.set(2016, 1, 29);
		assertEquals(target.getTime(), DateUtil.getLastDayOfMonth(cal.getTime()));
	}
}
