package com.akmans.trade.core.springdata.jpa.dao;

import com.akmans.trade.core.springdata.jpa.entities.MstScale;

public interface MstScaleDao extends BaseRepository<MstScale, Integer> {

	// Query by code
	MstScale findByCode(Integer code);
}
