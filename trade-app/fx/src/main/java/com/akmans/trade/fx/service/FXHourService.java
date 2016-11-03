package com.akmans.trade.fx.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.akmans.trade.core.enums.FXType;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.enums.OperationResult;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface FXHourService {
	public TrnFXHour operation(TrnFXHour tick, OperationMode mode) throws TradeException;

	public Optional<TrnFXHour> findOne(FXTickKey key);

	public Optional<TrnFXHour> findPrevious(FXTickKey key);

	public AbstractFXEntity generateFXPeriodData(FXType type, String currencyPair, LocalDateTime dateTimeFrom);

	public OperationResult refresh(String currencyPair, LocalDateTime currentDatetime, boolean bulkFlag)
			throws TradeException;
}
