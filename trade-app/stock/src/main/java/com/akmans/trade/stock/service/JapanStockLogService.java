package com.akmans.trade.stock.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.stock.dto.JapanStockLogQueryDto;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockLog;
import com.akmans.trade.stock.springdata.jpa.keys.JapanStockLogKey;

public interface JapanStockLogService {
	public Page<TrnJapanStockLog> findPage(JapanStockLogQueryDto criteria);

	public Optional<TrnJapanStockLog> findOne(JapanStockLogKey key);

	public TrnJapanStockLog operation(TrnJapanStockLog japanStockLog, OperationMode mode) throws TradeException;

//	public boolean exist(JapanStockLogKey key);

	public TrnJapanStockLog findMaxRegistDate(String jobId, Date processDate);
}
