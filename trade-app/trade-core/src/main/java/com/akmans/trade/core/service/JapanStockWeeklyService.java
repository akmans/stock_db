package com.akmans.trade.core.service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockWeekly;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockKey;

public interface JapanStockWeeklyService {

	public TrnJapanStockWeekly findOne(JapanStockKey key) throws TradeException;

	public void operation(TrnJapanStockWeekly stock, OperationMode mode) throws TradeException;

	public boolean exist(JapanStockKey key);
}
