package com.akmans.trade.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.MstSector17;

public interface Sector17Service {
	public List<MstSector17> findAll();

	public Page<MstSector17> findAll(Pageable pageRequest);

	public MstSector17 findOne(Integer code) throws TradeException;

	public void operation(MstSector17 scale, OperationMode mode) throws TradeException;
}
