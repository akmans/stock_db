package com.akmans.trade.core.utils;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.akmans.trade.core.config.TestCoreConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestCoreConfig.class, loader = AnnotationConfigContextLoader.class)
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
}
