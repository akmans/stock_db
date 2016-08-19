package com.akmans.trade.stock.springdata.jpa.repositories;

import java.util.List;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.stock.springdata.jpa.entities.MstSector33;

public interface MstSector33Repository extends BaseRepository<MstSector33, Integer> {

	List<MstSector33> findAllByOrderByCodeAsc();
}
