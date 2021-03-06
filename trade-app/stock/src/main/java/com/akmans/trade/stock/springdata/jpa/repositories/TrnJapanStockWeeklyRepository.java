package com.akmans.trade.stock.springdata.jpa.repositories;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockWeekly;
import com.akmans.trade.stock.springdata.jpa.keys.JapanStockKey;

public interface TrnJapanStockWeeklyRepository extends BaseRepository<TrnJapanStockWeekly, JapanStockKey> {
}
