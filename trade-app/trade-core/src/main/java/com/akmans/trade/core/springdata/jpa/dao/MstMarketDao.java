package com.akmans.trade.core.springdata.jpa.dao;

import java.util.List;

import com.akmans.trade.core.springdata.jpa.entities.MstMarket;

public interface MstMarketDao extends BaseRepository<MstMarket, Integer> {

	// Query all order by code
	List<MstMarket> findAllByOrderByCode();
}
