package com.akmans.trade.core.service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockMonthly;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockKey;

public interface JapanStockMonthlyService {

	public TrnJapanStockMonthly findOne(JapanStockKey key) throws TradeException;

	public void operation(TrnJapanStockMonthly stock, OperationMode mode) throws TradeException;

	public boolean exist(JapanStockKey key);
}
