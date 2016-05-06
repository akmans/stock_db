package com.akmans.trade.core.springdata.jpa.repositories;

import com.akmans.trade.core.springdata.jpa.entities.TrnStockDataMonthly;
import com.akmans.trade.core.springdata.jpa.keys.StockDataKey;

public interface TrnStockDataMonthlyDao extends BaseRepository<TrnStockDataMonthly, Integer> {

	// Query by primary key
	TrnStockDataMonthly findByStockDataKey(StockDataKey key);
}
