package com.akmans.trade.cashing.springdata.jpa.repositories;

import java.util.List;

import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;

public interface TrnSpecialItemRepository extends BaseRepository<TrnSpecialItem, Integer> {
	List<TrnSpecialItem> findAllByOrderByCodeAsc();
}
