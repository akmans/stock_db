package com.akmans.trade.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.akmans.trade.core.dto.CalendarQueryDto;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.MstCalendar;

public interface CalendarService {
	public Page<MstCalendar> findAll(Pageable pageRequest);

	public Page<MstCalendar> findPage(CalendarQueryDto criteria);

	public MstCalendar findOne(Long code) throws TradeException;

	public void operation(MstCalendar calendar, OperationMode mode) throws TradeException;
}
