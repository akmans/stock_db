package com.akmans.trade.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.akmans.trade.core.dto.SpecialDetailQueryDto;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialDetail;

public interface SpecialDetailService {
	public Page<TrnSpecialDetail> findAll(Pageable pageRequest);

	public Page<TrnSpecialDetail> findPage(SpecialDetailQueryDto criteria);

	public TrnSpecialDetail findOne(Long code) throws TradeException;

	public void operation(TrnSpecialDetail scale, OperationMode mode) throws TradeException;

	public TrnSpecialDetail findOneEager(Long code) throws TradeException;
}
