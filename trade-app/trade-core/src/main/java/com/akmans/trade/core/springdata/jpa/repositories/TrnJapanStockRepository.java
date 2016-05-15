package com.akmans.trade.core.springdata.jpa.repositories;

import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockKey;

public interface TrnJapanStockRepository extends BaseRepository<TrnJapanStock, JapanStockKey> {

	// Query by primary key
//	TrnJapanStock findByJapanStockKey(JapanStockKey key);
}
