package com.akmans.trade.core.springdata.jpa.repositories;

import java.util.List;

import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialItem;

public interface TrnSpecialItemRepository extends BaseRepository<TrnSpecialItem, Integer> {
	List<TrnSpecialItem> findAllByOrderByCodeAsc();
}
