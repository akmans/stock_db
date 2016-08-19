package com.akmans.trade.core.springdata.auditing;

import java.time.ZonedDateTime;

public class CurrentTimeDateTimeService implements DateTimeService {
	public ZonedDateTime getCurrentDateAndTime() {
		return ZonedDateTime.now();
	}
}
