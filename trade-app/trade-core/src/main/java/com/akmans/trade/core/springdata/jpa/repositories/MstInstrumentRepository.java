package com.akmans.trade.core.springdata.jpa.repositories;

import java.util.List;

import com.akmans.trade.core.springdata.jpa.entities.MstInstrument;

public interface MstInstrumentRepository extends BaseRepository<MstInstrument, Long> {

	// Query one eagerly.
	List<Object[]> findOneEager(Long code);
}
