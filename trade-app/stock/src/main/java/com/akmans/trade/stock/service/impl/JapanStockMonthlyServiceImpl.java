package com.akmans.trade.stock.service.impl;

import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.stock.service.JapanStockMonthlyService;
import com.akmans.trade.stock.springdata.jpa.repositories.TrnJapanStockMonthlyRepository;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockMonthly;
import com.akmans.trade.stock.springdata.jpa.keys.JapanStockKey;

@Service
public class JapanStockMonthlyServiceImpl implements JapanStockMonthlyService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockMonthlyServiceImpl.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private TrnJapanStockMonthlyRepository trnJapanStockMonthlyRepository;

	public TrnJapanStockMonthly findOne(JapanStockKey key) throws TradeException {
		Optional<TrnJapanStockMonthly> option = trnJapanStockMonthlyRepository.findOne(key);
		if (option.isPresent()) {
			return option.get();
		} else {
			throw new TradeException(
					messageService.getMessage("core.service.calendar.record.notfound", key.toString()));
		}
	}

	public void operation(TrnJapanStockMonthly stock, OperationMode mode) throws TradeException {
		logger.debug("the stock is {}", stock);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			Optional<TrnJapanStockMonthly> option = trnJapanStockMonthlyRepository.findOne(stock.getJapanStockKey());
			if (option.isPresent()) {
				throw new TradeException(messageService.getMessage("core.service.japanstock.record.alreadyexist",
						stock.getJapanStockKey().toString()));
			}
			logger.debug("before save");
			trnJapanStockMonthlyRepository.save(stock);
			logger.debug("after save");
			break;
		}
		case EDIT: {
			TrnJapanStockMonthly origin = findOne(stock.getJapanStockKey());
			if (!origin.getUpdatedDate().equals(stock.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.japanstock.record.inconsistent",
						stock.getJapanStockKey().toString()));
			}
			logger.debug("before save");
			trnJapanStockMonthlyRepository.save(stock);
			logger.debug("after save");
			break;
		}
		case DELETE: {
			TrnJapanStockMonthly origin = findOne(stock.getJapanStockKey());
			if (!origin.getUpdatedDate().equals(stock.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.japanstock.record.inconsistent",
						stock.getJapanStockKey().toString()));
			}
			trnJapanStockMonthlyRepository.delete(stock);
		}
		}
	}

	public boolean exist(JapanStockKey key) {
		return trnJapanStockMonthlyRepository.findOne(key).isPresent();
	}
}
