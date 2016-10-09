package com.akmans.trade.stock.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.stock.dto.InstrumentQueryDto;
import com.akmans.trade.stock.springdata.jpa.entities.MstInstrument;

public interface InstrumentService {
	public List<MstInstrument> findAll();

	public Page<MstInstrument> findPage(InstrumentQueryDto dto);

	public Optional<MstInstrument> findOne(Long code);

	public MstInstrument operation(MstInstrument scale, OperationMode mode) throws TradeException;

//	public MstInstrument findOneEager(Long code);
}
