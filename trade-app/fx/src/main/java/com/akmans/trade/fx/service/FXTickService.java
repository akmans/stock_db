package com.akmans.trade.fx.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.akmans.trade.core.enums.FXType;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;

public interface FXTickService {
	public TrnFXTick operation(TrnFXTick tick, OperationMode mode) throws TradeException;

	public Optional<TrnFXTick> findOne(Long code) throws TradeException;

	public AbstractFXEntity generateFXPeriodData(FXType type, String currencyPair, LocalDateTime dateTimeFrom);
}
