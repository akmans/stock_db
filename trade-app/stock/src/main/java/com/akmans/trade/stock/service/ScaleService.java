package com.akmans.trade.stock.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.stock.springdata.jpa.entities.MstScale;

public interface ScaleService {
	public List<MstScale> findAll();

	public Page<MstScale> findAll(Pageable pageRequest);

	public MstScale findOne(Integer code) throws TradeException;

	public void operation(MstScale scale, OperationMode mode) throws TradeException;
}
