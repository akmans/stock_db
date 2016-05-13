package com.akmans.trade.web.form;

import java.io.Serializable;

public class CalendarQueryForm extends AbstractQueryForm implements Serializable {
	private static final long serialVersionUID = 1L;

	/** calendar. */
	private String calendar;

	/** holiday */
	private Integer holiday;

	/**
	 * calendar getter.<BR>
	 * 
	 * @return calendar
	 */
	public String getCalendar() {
		return this.calendar;
	}

	/**
	 * calendar setter.<BR>
	 * 
	 * @param calendar
	 */
	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}

	/**
	 * holiday getter.<BR>
	 * 
	 * @return holiday
	 */
	public Integer getHoliday() {
		return this.holiday;
	}

	/**
	 * holiday setter.<BR>
	 * 
	 * @param holiday
	 */
	public void setHoliday(Integer holiday) {
		this.holiday = holiday;
	}

	@Override
	public String toString() {
		return "[calendar=" + calendar + ", holiday=" + holiday + "]";
	}
}
