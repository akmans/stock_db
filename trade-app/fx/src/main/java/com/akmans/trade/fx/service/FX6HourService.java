package com.akmans.trade.fx.service;

import java.util.Optional;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface FX6HourService {
	public void operation(TrnFX6Hour tick, OperationMode mode) throws TradeException;

	public Optional<TrnFX6Hour> findOne(FXTickKey key);
}
