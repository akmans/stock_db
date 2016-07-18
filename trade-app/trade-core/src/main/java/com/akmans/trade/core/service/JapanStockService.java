package com.akmans.trade.core.service;

import java.util.Date;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockMonthly;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockWeekly;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockKey;

public interface JapanStockService {

	public TrnJapanStock findOne(JapanStockKey key) throws TradeException;

	public void operation(TrnJapanStock stock, OperationMode mode) throws TradeException;

	public boolean exist(JapanStockKey key);

	public TrnJapanStockWeekly generateJapanStockWeeklyData(Integer code, Date dateFrom, Date dateTo);

	public TrnJapanStockMonthly generateJapanStockMonthlyData(Integer code, Date dateFrom, Date dateTo);
}
