package com.akmans.trade.core.springdata.jpa.repositories;

import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockMonthly;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockKey;

public interface TrnJapanStockMonthlyRepository extends BaseRepository<TrnJapanStockMonthly, Integer> {

	// Query by primary key
	TrnJapanStockMonthly findByJapanStockKey(JapanStockKey key);
}
