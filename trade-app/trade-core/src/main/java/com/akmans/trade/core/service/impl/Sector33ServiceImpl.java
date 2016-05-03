package com.akmans.trade.core.service.impl;

import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.Sector33Service;
import com.akmans.trade.core.springdata.jpa.dao.MstSector33Dao;
import com.akmans.trade.core.springdata.jpa.entities.MstSector33;
import com.akmans.trade.core.utils.CoreMessageUtils;

@Service
public class Sector33ServiceImpl implements Sector33Service {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ScaleServiceImpl.class);

	@Autowired
	private MstSector33Dao mstSector33Dao;

	public Page<MstSector33> findAll(Pageable pageable) {
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		return mstSector33Dao.findAll(pg);
	}

	public MstSector33 findOne(Integer code) throws TradeException {
		Optional<MstSector33> sector33 = mstSector33Dao.findOne(code);
		if (sector33.isPresent()) {
			return sector33.get();
		} else {
			throw new TradeException(CoreMessageUtils.getMessage("core.service.sector33.record.notfound", code));
		}
	}

	public void operation(MstSector33 sector33, OperationMode mode) throws TradeException {
		logger.debug("the scale is {}", sector33);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			if (mstSector33Dao.findOne(sector33.getCode()).isPresent()) {
				throw new TradeException(
						CoreMessageUtils.getMessage("core.service.sector33.record.alreadyexist", sector33.getCode()));
			}
			mstSector33Dao.save(sector33);
			break;
		}
		case EDIT: {
			MstSector33 origin = findOne(sector33.getCode());
			if (!origin.getUpdatedDate().equals(sector33.getUpdatedDate())) {
				throw new TradeException(
						CoreMessageUtils.getMessage("core.service.sector33.record.inconsistent", sector33.getCode()));
			}
			mstSector33Dao.save(sector33);
			break;
		}
		case DELETE: {
			MstSector33 origin = findOne(sector33.getCode());
			if (!origin.getUpdatedDate().equals(sector33.getUpdatedDate())) {
				throw new TradeException(
						CoreMessageUtils.getMessage("core.service.sector33.record.inconsistent", sector33.getCode()));
			}
			mstSector33Dao.delete(sector33);
		}
		}
	}
}
