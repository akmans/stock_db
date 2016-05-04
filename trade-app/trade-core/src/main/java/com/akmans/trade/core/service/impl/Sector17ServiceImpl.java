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
import com.akmans.trade.core.service.Sector17Service;
import com.akmans.trade.core.springdata.jpa.dao.MstSector17Repository;
import com.akmans.trade.core.springdata.jpa.entities.MstSector17;
import com.akmans.trade.core.utils.CoreMessageUtils;

@Service
public class Sector17ServiceImpl implements Sector17Service {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ScaleServiceImpl.class);

	@Autowired
	private MstSector17Repository mstSector17Repository;

	public Page<MstSector17> findAll(Pageable pageable) {
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		return mstSector17Repository.findAll(pg);
	}

	public MstSector17 findOne(Integer code) throws TradeException {
		Optional<MstSector17> sector17 = mstSector17Repository.findOne(code);
		if (sector17.isPresent()) {
			return sector17.get();
		} else {
			throw new TradeException(CoreMessageUtils.getMessage("core.service.sector17.record.notfound", code));
		}
	}

	public void operation(MstSector17 sector17, OperationMode mode) throws TradeException {
		logger.debug("the scale is {}", sector17);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			if (mstSector17Repository.findOne(sector17.getCode()).isPresent()) {
				throw new TradeException(
						CoreMessageUtils.getMessage("core.service.sector17.record.alreadyexist", sector17.getCode()));
			}
			mstSector17Repository.save(sector17);
			break;
		}
		case EDIT: {
			MstSector17 origin = findOne(sector17.getCode());
			if (!origin.getUpdatedDate().equals(sector17.getUpdatedDate())) {
				throw new TradeException(
						CoreMessageUtils.getMessage("core.service.sector17.record.inconsistent", sector17.getCode()));
			}
			mstSector17Repository.save(sector17);
			break;
		}
		case DELETE: {
			MstSector17 origin = findOne(sector17.getCode());
			if (!origin.getUpdatedDate().equals(sector17.getUpdatedDate())) {
				throw new TradeException(
						CoreMessageUtils.getMessage("core.service.sector17.record.inconsistent", sector17.getCode()));
			}
			mstSector17Repository.delete(sector17);
		}
		}
	}
}
