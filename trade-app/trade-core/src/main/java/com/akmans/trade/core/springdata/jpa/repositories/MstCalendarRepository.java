package com.akmans.trade.core.springdata.jpa.repositories;

import java.util.List;

import com.akmans.trade.core.springdata.jpa.entities.MstCalendar;

public interface MstCalendarRepository extends BaseRepository<MstCalendar, Long> {
	List<MstCalendar> findByCalendarAndHoliday(String calendar, int holiday);
}