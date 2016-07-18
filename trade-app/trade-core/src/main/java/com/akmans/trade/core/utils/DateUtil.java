package com.akmans.trade.core.utils;

import java.text.SimpleDateFormat;
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

	/**
	 * Get last day of week using provided specific date.
	 * @param date the provided date.
	 * @return Last day of week.
	 */
	public static Date getLastDayOfWeek(Date date) {
		// Calendar instance.
		Calendar cal = Calendar.getInstance(Locale.JAPAN);
		cal.clear();
		// Set date of calendar.
		cal.setTime(date);
		// Set day of week.
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		// Next week.
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		// Last day of current week.
		cal.add(Calendar.DAY_OF_YEAR, -1);
		// return last day of week.
		return cal.getTime();
	}

	/**
	 * Get first day of month using provided specific date.
	 * @param date the provided date.
	 * @return First day of month.
	 */
	public static Date getFirstDayOfMonth(Date date) {
		// Calendar instance.
		Calendar cal = Calendar.getInstance(Locale.JAPAN);
		cal.clear();
		// Set date of calendar.
		cal.setTime(date);
		// Set day of month.
		cal.set(Calendar.DATE, 1);
		// return first day of month.
		return cal.getTime();
	}

	/**
	 * Get last day of month using provided specific date.
	 * @param date the provided date.
	 * @return Last day of month.
	 */
	public static Date getLastDayOfMonth(Date date) {
		// Calendar instance.
		Calendar cal = Calendar.getInstance(Locale.JAPAN);
		cal.clear();
		// Set date of calendar.
		cal.setTime(date);
		// Set day of month.
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		// return last day of month.
		return cal.getTime();
	}

	public static String formatDate(Date date, String format) {
		if (date == null || format == null) {
			return null;
		}
		SimpleDateFormat formater =  new SimpleDateFormat (format);
		return formater.format(date);
	}
}
