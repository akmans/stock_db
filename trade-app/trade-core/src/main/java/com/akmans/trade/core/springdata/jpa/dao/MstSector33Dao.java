package com.akmans.trade.core.springdata.jpa.dao;

import com.akmans.trade.core.springdata.jpa.entities.MstSector33;

public interface MstSector33Dao extends BaseRepository<MstSector33, Integer> {

	// Query by code
	MstSector33 findByCode(Integer code);
}
