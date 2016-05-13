package com.akmans.trade.web.form;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Min;
//import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.enums.Calendar;
import com.akmans.trade.core.enums.JapanHoliday;
import com.akmans.trade.core.utils.DateUtil;

public class CalendarEntryForm extends AbstractSimpleForm implements Serializable {
	private static final long serialVersionUID = 1L;

	/** code. */
	// @NotNull(message="{form.calendarform.code.notnull}")
	// @Min(value = 1, message="{form.calendarform.code.min}")
	private Long code;

	/** calendar. */
	@Size(min = 1, message = "{form.calendarform.calendar.notnull}")
	private String calendar;

	@NotNull(message = "{form.calendarform.registat.notnull}")
	private Date registAt;

	@NotNull(message = "{form.calendarform.holiday.notnull}")
	private Integer holiday;

	@Size(min = 0, max = 100, message = "{form.calendarform.description.length}")
	private String description;

	/**
	 * code getter.<BR>
	 * 
	 * @return code
	 */
	public Long getCode() {
		return this.code;
	}

	/**
	 * code setter.<BR>
	 * 
	 * @param code
	 */
	public void setCode(Long code) {
		this.code = code;
	}

	/**
	 * calendar getter.<BR>
	 * 
	 * @return calendar
	 */
	public String getCalendar() {
		return this.calendar;
	}

	public String getCalendarLabel() {
		if (this.calendar == null) {
			return null;
		}
		// Get calendars.
		Map<String, String> calendars = new HashMap<String, String>();
		for (Calendar calendar : EnumSet.allOf(Calendar.class)) {
			calendars.put(calendar.getValue(), calendar.getLabel());
		}
		return calendars.get(this.calendar);
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

	public String getHolidayLabel() {
		if (this.holiday == null) {
			return null;
		}
		// Get holidays.
		Map<Integer, String> holidays = new HashMap<Integer, String>();
		for (JapanHoliday holiday : EnumSet.allOf(JapanHoliday.class)) {
			holidays.put(holiday.getValue(), holiday.getLabel());
		}
		return holidays.get(this.holiday);
	}

	/**
	 * holiday setter.<BR>
	 * 
	 * @param holiday
	 */
	public void setHoliday(Integer holiday) {
		this.holiday = holiday;
	}

	/**
	 * registAt getter.<BR>
	 * 
	 * @return registAt
	 */
	public Date getRegistAt() {
		return this.registAt;
	}

	public String getDisRegistAt() {
		if (this.registAt == null) {
			return null;
		} else {
			return DateUtil.formatDate(registAt, Constants.UI_DATE_FORMAT);
		}
	}

	/**
	 * registAt setter.<BR>
	 * 
	 * @param registAt
	 */
	public void setRegistAt(Date registAt) {
		this.registAt = registAt;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public String toString() {
		return "[code=" + code + ", calendar=" + calendar + ", holiday=" + holiday + ", registAt=" + registAt
				+ ", description=" + description + super.toString() + "]";
	}
}
