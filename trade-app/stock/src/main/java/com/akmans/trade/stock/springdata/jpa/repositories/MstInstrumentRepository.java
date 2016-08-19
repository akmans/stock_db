package com.akmans.trade.stock.springdata.jpa.repositories;

import java.util.List;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.stock.springdata.jpa.entities.MstInstrument;

public interface MstInstrumentRepository extends BaseRepository<MstInstrument, Long> {

	// Query one eagerly.
	List<Object[]> findOneEager(Long code);
}
