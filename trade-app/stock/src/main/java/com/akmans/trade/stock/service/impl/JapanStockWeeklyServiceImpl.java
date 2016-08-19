package com.akmans.trade.stock.service.impl;

import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.stock.service.JapanStockWeeklyService;
import com.akmans.trade.stock.springdata.jpa.repositories.TrnJapanStockWeeklyRepository;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockWeekly;
import com.akmans.trade.stock.springdata.jpa.keys.JapanStockKey;

@Service
public class JapanStockWeeklyServiceImpl implements JapanStockWeeklyService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockWeeklyServiceImpl.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private TrnJapanStockWeeklyRepository trnJapanStockWeeklyRepository;

	public TrnJapanStockWeekly findOne(JapanStockKey key) throws TradeException {
		Optional<TrnJapanStockWeekly> option = trnJapanStockWeeklyRepository.findOne(key);
		if (option.isPresent()) {
			return option.get();
		} else {
			throw new TradeException(
					messageService.getMessage("core.service.calendar.record.notfound", key.toString()));
		}
	}

	public void operation(TrnJapanStockWeekly stock, OperationMode mode) throws TradeException {
		logger.debug("the stock is {}", stock);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			Optional<TrnJapanStockWeekly> option = trnJapanStockWeeklyRepository.findOne(stock.getJapanStockKey());
			if (option.isPresent()) {
				throw new TradeException(messageService.getMessage("core.service.japanstock.record.alreadyexist",
						stock.getJapanStockKey().toString()));
			}
			logger.debug("before save");
			trnJapanStockWeeklyRepository.save(stock);
			logger.debug("after save");
			break;
		}
		case EDIT: {
			TrnJapanStockWeekly origin = findOne(stock.getJapanStockKey());
			if (!origin.getUpdatedDate().equals(stock.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.japanstock.record.inconsistent",
						stock.getJapanStockKey().toString()));
			}
			logger.debug("before save");
			trnJapanStockWeeklyRepository.save(stock);
			logger.debug("after save");
			break;
		}
		case DELETE: {
			TrnJapanStockWeekly origin = findOne(stock.getJapanStockKey());
			if (!origin.getUpdatedDate().equals(stock.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.japanstock.record.inconsistent",
						stock.getJapanStockKey().toString()));
			}
			trnJapanStockWeeklyRepository.delete(stock);
		}
		}
	}

	public boolean exist(JapanStockKey key) {
		return trnJapanStockWeeklyRepository.findOne(key).isPresent();
	}
}
