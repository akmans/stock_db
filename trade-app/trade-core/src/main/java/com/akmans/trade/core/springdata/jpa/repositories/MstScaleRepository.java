package com.akmans.trade.core.springdata.jpa.repositories;

import java.util.List;

import com.akmans.trade.core.springdata.jpa.entities.MstScale;

public interface MstScaleRepository extends BaseRepository<MstScale, Integer> {

	List<MstScale> findAllByOrderByCodeAsc();
}
