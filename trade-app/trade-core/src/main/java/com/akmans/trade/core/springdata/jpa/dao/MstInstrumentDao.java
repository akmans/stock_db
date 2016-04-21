package com.akmans.trade.core.springdata.jpa.dao;

import com.akmans.trade.core.springdata.jpa.entities.MstInstrument;

public interface MstInstrumentDao extends BaseRepository<MstInstrument, Integer> {

	// Query by code
	MstInstrument findByCode(Integer code);
}
