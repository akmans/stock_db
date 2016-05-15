package com.akmans.trade.core.service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;

public interface JapanStockService {

	public void operation(TrnJapanStock stock, OperationMode mode) throws TradeException;
}
