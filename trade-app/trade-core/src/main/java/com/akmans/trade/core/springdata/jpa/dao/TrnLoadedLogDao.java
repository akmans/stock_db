package com.akmans.trade.core.springdata.jpa.dao;

import com.akmans.trade.core.springdata.jpa.entities.TrnLoadedLog;

public interface TrnLoadedLogDao extends BaseRepository<TrnLoadedLog, Integer> {

	// Query by code
	TrnLoadedLog findByCode(Integer code);
}
