package com.akmans.trade.fx.service;

import java.time.ZonedDateTime;

import com.akmans.trade.core.enums.FXType;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface FXTickService {
	public TrnFXTick operation(TrnFXTick tick, OperationMode mode) throws TradeException;

	public TrnFXTick findOne(FXTickKey key) throws TradeException;

	public boolean exist(FXTickKey key);

	public AbstractFXEntity generateFXPeriodData(FXType type, String currencyPair, ZonedDateTime dateTimeFrom);
}
