package com.akmans.trade.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.MstInstrument;

public interface InstrumentService {
	public Page<MstInstrument> findAll(Pageable pageRequest);

	public Page<MstInstrument> findPage(Pageable pageRequest);

	public MstInstrument findOne(Integer code) throws TradeException;

	public void operation(MstInstrument scale, OperationMode mode) throws TradeException;
}