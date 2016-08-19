package com.akmans.trade.core.springdata.auditing;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.data.auditing.DateTimeProvider;

public class AuditingDateTimeProvider implements DateTimeProvider {

	private final DateTimeService dateTimeService;

	public AuditingDateTimeProvider(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	public Calendar getNow() {
		return GregorianCalendar.from(dateTimeService.getCurrentDateAndTime());
	}
}
