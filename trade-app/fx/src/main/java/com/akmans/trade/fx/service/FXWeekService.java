package com.akmans.trade.fx.service;

import java.util.Optional;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface FXWeekService {
	public void operation(TrnFXWeek tick, OperationMode mode) throws TradeException;

	public Optional<TrnFXWeek> findOne(FXTickKey key);

	public Optional<TrnFXWeek> findPrevious(FXTickKey key);
}
