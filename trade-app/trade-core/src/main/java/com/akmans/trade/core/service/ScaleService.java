package com.akmans.trade.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.MstScale;

public interface ScaleService {
	public Page<MstScale> findAll(Pageable pageRequest);

	public MstScale findOne(Integer code) throws TradeException;

	public void operation(MstScale scale, OperationMode mode) throws TradeException;
}
