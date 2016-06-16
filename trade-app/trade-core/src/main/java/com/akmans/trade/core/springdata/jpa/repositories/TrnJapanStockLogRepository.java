package com.akmans.trade.core.springdata.jpa.repositories;

import java.util.Date;

import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockLog;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockLogKey;

public interface TrnJapanStockLogRepository extends BaseRepository<TrnJapanStockLog, JapanStockLogKey> {
	TrnJapanStockLog findMaxRegistDate(String jobId, Date processDate);
}
