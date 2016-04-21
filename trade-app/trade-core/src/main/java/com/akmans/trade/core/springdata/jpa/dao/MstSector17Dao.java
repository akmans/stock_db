package com.akmans.trade.core.springdata.jpa.dao;

import com.akmans.trade.core.springdata.jpa.entities.MstSector17;

public interface MstSector17Dao extends BaseRepository<MstSector17, Integer> {

	// Query by code
	MstSector17 findByCode(Integer code);
}
