package com.akmans.trade.core.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	/**
	 * Get first day of week using provided specific date.
	 * @param date the provided date.
	 * @return First day of week.
	 */
	public static Date getFirstDayOfWeek(Date date) {
		// Calendar instance.
		Calendar cal = Calendar.getInstance(Locale.JAPAN);
		cal.clear();
		// Set date of calendar.
		cal.setTime(date);
		// Set day of week.
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		// return first day of week.
		return cal.getTime();
	}
}
