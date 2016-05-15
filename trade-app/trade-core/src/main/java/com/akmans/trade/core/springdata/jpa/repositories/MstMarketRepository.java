package com.akmans.trade.core.springdata.jpa.repositories;

import java.util.List;

import com.akmans.trade.core.springdata.jpa.entities.MstMarket;

public interface MstMarketRepository extends BaseRepository<MstMarket, Integer> {

	List<MstMarket> findAllByOrderByCodeAsc();
}
