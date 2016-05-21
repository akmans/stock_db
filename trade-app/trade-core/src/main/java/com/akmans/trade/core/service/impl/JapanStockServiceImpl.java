package com.akmans.trade.core.service.impl;

import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.JapanStockService;
import com.akmans.trade.core.springdata.jpa.repositories.TrnJapanStockRepository;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockKey;
import com.akmans.trade.core.utils.CoreMessageUtils;

@Service
public class JapanStockServiceImpl implements JapanStockService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockServiceImpl.class);

	@Autowired
	private TrnJapanStockRepository trnJapanStockRepository;

	public TrnJapanStock findOne(JapanStockKey key) throws TradeException {
		Optional<TrnJapanStock> option = trnJapanStockRepository.findOne(key);
		if (option.isPresent()) {
			return option.get();
		} else {
			throw new TradeException(
					CoreMessageUtils.getMessage("core.service.calendar.record.notfound", key.toString()));
		}
	}

	public void operation(TrnJapanStock stock, OperationMode mode) throws TradeException {
		logger.debug("the stock is {}", stock);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			Optional<TrnJapanStock> option = trnJapanStockRepository.findOne(stock.getJapanStockKey());
			if (option.isPresent()) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.japanstock.record.alreadyexist",
						stock.getJapanStockKey().toString()));
			}
			trnJapanStockRepository.save(stock);
			break;
		}
		case EDIT: {
			TrnJapanStock origin = findOne(stock.getJapanStockKey());
			if (!origin.getUpdatedDate().equals(stock.getUpdatedDate())) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.japanstock.record.inconsistent",
						stock.getJapanStockKey().toString()));
			}
			trnJapanStockRepository.save(stock);
			break;
		}
		case DELETE: {
			TrnJapanStock origin = findOne(stock.getJapanStockKey());
			if (!origin.getUpdatedDate().equals(stock.getUpdatedDate())) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.japanstock.record.inconsistent",
						stock.getJapanStockKey().toString()));
			}
			trnJapanStockRepository.delete(stock);
		}
		}
	}
}
