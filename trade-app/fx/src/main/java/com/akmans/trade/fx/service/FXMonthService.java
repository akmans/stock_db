package com.akmans.trade.fx.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.enums.OperationResult;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface FXMonthService {
	public TrnFXMonth operation(TrnFXMonth tick, OperationMode mode) throws TradeException;

	public Optional<TrnFXMonth> findOne(FXTickKey key);

	public Optional<TrnFXMonth> findPrevious(FXTickKey key);

	public OperationResult refresh(String currencyPair, LocalDateTime currentDatetime) throws TradeException;
}
