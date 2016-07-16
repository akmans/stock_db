package com.akmans.trade.core.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.akmans.trade.core.dto.InstrumentQueryDto;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.MstInstrument;

public interface InstrumentService {
	public List<MstInstrument> findAll();

	public Page<MstInstrument> findPage(InstrumentQueryDto dto);

	public MstInstrument findOne(Long code) throws TradeException;

	public void operation(MstInstrument scale, OperationMode mode) throws TradeException;

	public MstInstrument findOneEager(Long code) throws TradeException;
}
