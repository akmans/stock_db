package com.akmans.trade.core.springdata.auditing;

import java.time.ZonedDateTime;

public interface DateTimeService {
	ZonedDateTime getCurrentDateAndTime();
}
