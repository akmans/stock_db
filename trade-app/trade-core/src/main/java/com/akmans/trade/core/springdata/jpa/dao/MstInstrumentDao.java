package com.akmans.trade.core.springdata.jpa.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.akmans.trade.core.springdata.jpa.entities.MstInstrument;

public interface MstInstrumentDao extends BaseRepository<MstInstrument, Integer> {

	// Query by code
	List<Object[]> findPage(Pageable pageRequest);
}
