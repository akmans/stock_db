package com.akmans.trade.core.service;

import java.util.Date;

import org.springframework.data.domain.Page;

import com.akmans.trade.core.dto.JapanStockLogQueryDto;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockLog;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockLogKey;

public interface JapanStockLogService {
	public Page<TrnJapanStockLog> findPage(JapanStockLogQueryDto criteria);

	public TrnJapanStockLog findOne(JapanStockLogKey key) throws TradeException;

	public void operation(TrnJapanStockLog japanStockLog, OperationMode mode) throws TradeException;

	public boolean exist(JapanStockLogKey key);

	public TrnJapanStockLog findMaxRegistDate(String jobId, Date processDate);
}
