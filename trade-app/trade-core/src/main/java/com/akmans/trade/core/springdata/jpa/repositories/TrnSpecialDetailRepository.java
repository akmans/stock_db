package com.akmans.trade.core.springdata.jpa.repositories;

import java.util.List;

import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialDetail;

public interface TrnSpecialDetailRepository extends BaseRepository<TrnSpecialDetail, Long> {
	// Query one eagerly.
	List<Object[]> findOneEager(Long code);
}
