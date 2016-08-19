package com.akmans.trade.stock.springdata.jpa.repositories;

import java.util.List;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.stock.springdata.jpa.entities.MstSector17;

public interface MstSector17Repository extends BaseRepository<MstSector17, Integer> {

	List<MstSector17> findAllByOrderByCodeAsc();
}
