package com.akmans.trade.core.springdata.jpa.repositories;

import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockWeekly;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockKey;

public interface TrnJapanStockWeeklyRepository extends BaseRepository<TrnJapanStockWeekly, Integer> {

	// Query by primary key
//	TrnJapanStockWeekly findByStockDataKey(JapanStockKey key);
}
