package com.akmans.trade.core.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;

public class DateUtilTest {
	public void testGetFirstDayOfWeek() {
	    Calendar cal = Calendar.getInstance(Locale.JAPAN);
	    // Monday (target day)
	    cal.clear();
	    cal.set(2016, 0, 17);
	    Date targetDate = cal.getTime();

	    // Test Monday
	    Assert.assertEquals(targetDate, DateUtil.getFirstDayOfWeek(cal.getTime()));
	    // Test Tuesday
	    cal.set(2016, 0, 18);
	    Assert.assertEquals(targetDate, DateUtil.getFirstDayOfWeek(cal.getTime()));
	    // Test Wednesday
	    cal.set(2016, 0, 19);
	    Assert.assertEquals(targetDate, DateUtil.getFirstDayOfWeek(cal.getTime()));
	    // Test Thursday
	    cal.set(2016, 0, 20);
	    Assert.assertEquals(targetDate, DateUtil.getFirstDayOfWeek(cal.getTime()));
	    // Test Friday
	    cal.set(2016, 0, 21);
	    Assert.assertEquals(targetDate, DateUtil.getFirstDayOfWeek(cal.getTime()));
	    // Test Saturday
	    cal.set(2016, 0, 22);
	    Assert.assertEquals(targetDate, DateUtil.getFirstDayOfWeek(cal.getTime()));
	    // Test Sunday
	    cal.set(2016, 0, 23);
	    Assert.assertEquals(targetDate, DateUtil.getFirstDayOfWeek(cal.getTime()));
	}
}
