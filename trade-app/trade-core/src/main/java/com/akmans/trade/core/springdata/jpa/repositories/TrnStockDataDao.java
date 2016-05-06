package com.akmans.trade.core.springdata.jpa.repositories;

import com.akmans.trade.core.springdata.jpa.entities.TrnStockData;
import com.akmans.trade.core.springdata.jpa.keys.StockDataKey;

public interface TrnStockDataDao extends BaseRepository<TrnStockData, Integer> {

	// Query by primary key
	TrnStockData findByStockDataKey(StockDataKey key);
}
