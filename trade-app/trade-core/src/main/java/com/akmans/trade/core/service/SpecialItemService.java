package com.akmans.trade.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialItem;

public interface SpecialItemService {
	public Page<TrnSpecialItem> findAll(Pageable pageRequest);

	public List<TrnSpecialItem> findAll();

	public TrnSpecialItem findOne(Integer code) throws TradeException;

	public void operation(TrnSpecialItem specialItem, OperationMode mode) throws TradeException;
}
