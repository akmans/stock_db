package com.akmans.trade.stock.springdata.jpa.repositories;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.stock.springdata.jpa.entities.TrnLoadedLog;

public interface TrnLoadedLogDao extends BaseRepository<TrnLoadedLog, Integer> {

	// Query by code
	TrnLoadedLog findByCode(Integer code);
}
