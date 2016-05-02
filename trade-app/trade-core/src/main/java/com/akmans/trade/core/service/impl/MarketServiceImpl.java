package com.akmans.trade.core.service.impl;

import java.util.Locale;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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
		Locale locale = LocaleContextHolder.getLocale();
		logger.debug("Locale in MarketServiceImpl = {}", locale);
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

	public void save(MstMarket market, OperationMode mode) throws TradeException {
		logger.debug("the market is {}", market);
		logger.debug("the mode is {}", mode);
		if(mode == OperationMode.EDIT) {
			MstMarket origin = findOne(market.getCode());
			if (!origin.getUpdatedDate().equals(market.getUpdatedDate())) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.market.record.inconsistent", market.getCode()));
			}
		} else if (mode == OperationMode.NEW && mstMarketDao.findOne(market.getCode()).isPresent()) {
			throw new TradeException(CoreMessageUtils.getMessage("core.service.market.record.alreadyexist", market.getCode()));
		}
		mstMarketDao.save(market);
	}

	public void delete(Integer code) throws TradeException {
		mstMarketDao.delete(findOne(code));
	}
}
