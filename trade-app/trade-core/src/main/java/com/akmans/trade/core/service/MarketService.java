package com.akmans.trade.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;

public interface MarketService {
	public List<MstMarket> findAll();

	public Page<MstMarket> findAll(Pageable pageRequest);

	public MstMarket findOne(Integer code) throws TradeException;

	public void operation(MstMarket martket, OperationMode mode) throws TradeException;
}
