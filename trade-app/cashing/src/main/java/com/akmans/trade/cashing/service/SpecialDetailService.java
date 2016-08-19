package com.akmans.trade.cashing.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.akmans.trade.cashing.dto.SpecialDetailQueryDto;
import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialDetail;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;

public interface SpecialDetailService {
	public Page<TrnSpecialDetail> findAll(Pageable pageRequest);

	public Page<TrnSpecialDetail> findPage(SpecialDetailQueryDto criteria);

	public TrnSpecialDetail findOne(Long code) throws TradeException;

	public void operation(TrnSpecialDetail scale, OperationMode mode) throws TradeException;

	public TrnSpecialDetail findOneEager(Long code) throws TradeException;
}
