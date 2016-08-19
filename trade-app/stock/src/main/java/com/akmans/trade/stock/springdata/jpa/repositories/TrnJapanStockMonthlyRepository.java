package com.akmans.trade.stock.springdata.jpa.repositories;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockMonthly;
import com.akmans.trade.stock.springdata.jpa.keys.JapanStockKey;

public interface TrnJapanStockMonthlyRepository extends BaseRepository<TrnJapanStockMonthly, JapanStockKey> {
}
