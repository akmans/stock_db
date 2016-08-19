package com.akmans.trade.stock.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.stock.springdata.jpa.entities.MstSector33;

public interface Sector33Service {
	public List<MstSector33> findAll();

	public Page<MstSector33> findAll(Pageable pageRequest);

	public MstSector33 findOne(Integer code) throws TradeException;

	public void operation(MstSector33 scale, OperationMode mode) throws TradeException;
}
