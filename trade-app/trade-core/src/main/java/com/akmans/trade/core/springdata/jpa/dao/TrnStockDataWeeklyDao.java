package com.akmans.trade.core.springdata.jpa.dao;

import com.akmans.trade.core.springdata.jpa.entities.TrnStockDataWeekly;
import com.akmans.trade.core.springdata.jpa.keys.StockDataKey;

public interface TrnStockDataWeeklyDao extends BaseRepository<TrnStockDataWeekly, Integer> {

	// Query by primary key
	TrnStockDataWeekly findByStockDataKey(StockDataKey key);
}
