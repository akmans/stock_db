package com.akmans.trade.cashing.springdata.jpa.repositories;

import java.util.List;

import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialDetail;
import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;

public interface TrnSpecialDetailRepository extends BaseRepository<TrnSpecialDetail, Long> {
	// Query one eagerly.
	List<Object[]> findOneEager(Long code);
}
