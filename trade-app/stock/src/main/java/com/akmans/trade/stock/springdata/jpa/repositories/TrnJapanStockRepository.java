package com.akmans.trade.stock.springdata.jpa.repositories;

import java.util.Date;
import java.util.List;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.stock.springdata.jpa.keys.JapanStockKey;

public interface TrnJapanStockRepository extends BaseRepository<TrnJapanStock, JapanStockKey> {
	// Find stock data in period.
	List<TrnJapanStock> findJapanStockInPeriod(Integer code, Date dateFrom, Date dateTo);
}
