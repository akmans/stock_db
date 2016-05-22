package com.akmans.trade.core.service.impl;

import java.util.List;
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
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.core.springdata.jpa.repositories.MstMarketRepository;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;

@Service
public class MarketServiceImpl implements MarketService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketServiceImpl.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private MstMarketRepository mstMarketRepository;

	public List<MstMarket> findAll() {
		return mstMarketRepository.findAllByOrderByCodeAsc();
	}

	public Page<MstMarket> findAll(Pageable pageable) {
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		return mstMarketRepository.findAll(pg);
	}

	public MstMarket findOne(Integer code) throws TradeException {
		Optional<MstMarket> market = mstMarketRepository.findOne(code);
		if (market.isPresent()) {
			return market.get();
		} else {
			throw new TradeException(messageService.getMessage("core.service.market.record.notfound", code));
		}
	}

	public void operation(MstMarket market, OperationMode mode) throws TradeException {
		logger.debug("the market is {}", market);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			if (mstMarketRepository.findOne(market.getCode()).isPresent()) {
				throw new TradeException(
						messageService.getMessage("core.service.market.record.alreadyexist", market.getCode()));
			}
			mstMarketRepository.save(market);
			break;
		}
		case EDIT: {
			MstMarket origin = findOne(market.getCode());
			if (!origin.getUpdatedDate().equals(market.getUpdatedDate())) {
				throw new TradeException(
						messageService.getMessage("core.service.market.record.inconsistent", market.getCode()));
			}
			mstMarketRepository.save(market);
			break;
		}
		case DELETE: {
			MstMarket origin = findOne(market.getCode());
			if (!origin.getUpdatedDate().equals(market.getUpdatedDate())) {
				throw new TradeException(
						messageService.getMessage("core.service.market.record.inconsistent", market.getCode()));
			}
			mstMarketRepository.delete(market);
		}
		}
	}

	public MstMarket findByName(String name) throws TradeException {
		List<MstMarket> markets = mstMarketRepository.findByName(name);
		if (markets == null || markets.size() == 0) {
//			throw new TradeException(CoreMessageUtils.getMessage("core.service.market.record.notfound.by.name", name));
			return null;
		} else if (markets.size() > 1) {
			throw new TradeException(messageService.getMessage("core.service.market.record.found.duplicated", name));
		}
		return markets.get(0);
	}
}
