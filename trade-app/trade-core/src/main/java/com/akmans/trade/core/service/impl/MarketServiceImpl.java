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
import com.akmans.trade.core.service.MarketService;
import com.akmans.trade.core.springdata.jpa.dao.MstMarketDao;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;
import com.akmans.trade.core.utils.CoreMessageUtils;

@Service
public class MarketServiceImpl implements MarketService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketServiceImpl.class);

	@Autowired
	private MstMarketDao mstMarketDao;

	public Page<MstMarket> findAll(Pageable pageable) {
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		return mstMarketDao.findAll(pg);
	}

	public MstMarket findOne(Integer code) throws TradeException {
		Optional<MstMarket> market = mstMarketDao.findOne(code);
		if (market.isPresent()) {
			return market.get();
		} else {
			throw new TradeException(CoreMessageUtils.getMessage("core.service.market.record.notfound", code));
		}
	}

	public void operation(MstMarket market, OperationMode mode) throws TradeException {
		logger.debug("the market is {}", market);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			if (mstMarketDao.findOne(market.getCode()).isPresent()) {
				throw new TradeException(
						CoreMessageUtils.getMessage("core.service.market.record.alreadyexist", market.getCode()));
			}
			mstMarketDao.save(market);
			break;
		}
		case EDIT: {
			MstMarket origin = findOne(market.getCode());
			if (!origin.getUpdatedDate().equals(market.getUpdatedDate())) {
				throw new TradeException(
						CoreMessageUtils.getMessage("core.service.market.record.inconsistent", market.getCode()));
			}
			mstMarketDao.save(market);
			break;
		}
		case DELETE: {
			MstMarket origin = findOne(market.getCode());
			if (!origin.getUpdatedDate().equals(market.getUpdatedDate())) {
				throw new TradeException(
						CoreMessageUtils.getMessage("core.service.market.record.inconsistent", market.getCode()));
			}
			mstMarketDao.delete(market);
		}
		}
	}
}
