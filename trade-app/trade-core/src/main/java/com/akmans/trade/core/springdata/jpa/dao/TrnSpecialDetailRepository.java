package com.akmans.trade.core.springdata.jpa.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialDetail;

public interface TrnSpecialDetailRepository extends BaseRepository<TrnSpecialDetail, Long> {
	// Query with pagination.
	List<Object[]> findPage(Pageable pageRequest);

	// Query one eagerly.
	List<Object[]> findOneEager(Long code);
}
