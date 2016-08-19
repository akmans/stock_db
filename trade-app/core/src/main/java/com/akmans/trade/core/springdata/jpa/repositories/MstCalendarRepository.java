package com.akmans.trade.core.springdata.jpa.repositories;

import java.util.Date;
import java.util.List;

import com.akmans.trade.core.springdata.jpa.entities.MstCalendar;

public interface MstCalendarRepository extends BaseRepository<MstCalendar, Long> {

	List<MstCalendar> findByCalendarAndRegistAt(String calendar, Date registAt);

	List<MstCalendar> findByCalendarAndHolidayAndRegistAt(String calendar, int holiday, Date registAt);
}