package com.akmans.trade.stock.springdata.jpa.repositories;

import java.util.List;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.stock.springdata.jpa.entities.MstMarket;

public interface MstMarketRepository extends BaseRepository<MstMarket, Integer> {

	List<MstMarket> findAllByOrderByCodeAsc();

	List<MstMarket> findByName(String name);
}
