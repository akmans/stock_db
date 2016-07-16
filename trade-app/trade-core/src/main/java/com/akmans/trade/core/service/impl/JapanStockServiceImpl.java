package com.akmans.trade.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.JapanStockService;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.core.springdata.jpa.repositories.TrnJapanStockRepository;
import com.akmans.trade.core.utils.DateUtil;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockWeekly;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockKey;

@Service
public class JapanStockServiceImpl implements JapanStockService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockServiceImpl.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private TrnJapanStockRepository trnJapanStockRepository;

	public TrnJapanStock findOne(JapanStockKey key) throws TradeException {
		Optional<TrnJapanStock> option = trnJapanStockRepository.findOne(key);
		if (option.isPresent()) {
			return option.get();
		} else {
			throw new TradeException(
					messageService.getMessage("core.service.calendar.record.notfound", key.toString()));
		}
	}

	public void operation(TrnJapanStock stock, OperationMode mode) throws TradeException {
		logger.debug("the stock is {}", stock);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			Optional<TrnJapanStock> option = trnJapanStockRepository.findOne(stock.getJapanStockKey());
			if (option.isPresent()) {
				throw new TradeException(messageService.getMessage("core.service.japanstock.record.alreadyexist",
						stock.getJapanStockKey().toString()));
			}
			trnJapanStockRepository.save(stock);
			break;
		}
		case EDIT: {
			TrnJapanStock origin = findOne(stock.getJapanStockKey());
			if (!origin.getUpdatedDate().equals(stock.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.japanstock.record.inconsistent",
						stock.getJapanStockKey().toString()));
			}
			trnJapanStockRepository.save(stock);
			break;
		}
		case DELETE: {
			TrnJapanStock origin = findOne(stock.getJapanStockKey());
			if (!origin.getUpdatedDate().equals(stock.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.japanstock.record.inconsistent",
						stock.getJapanStockKey().toString()));
			}
			trnJapanStockRepository.delete(stock);
		}
		}
	}

	public boolean exist(JapanStockKey key) {
		return trnJapanStockRepository.findOne(key).isPresent();
	}

	public TrnJapanStockWeekly generateJapanStockWeeklyData(Integer code, Date dateFrom, Date dateTo) {
		List<TrnJapanStock> japanStocks = trnJapanStockRepository.findJapanStockInPeriod(code, dateFrom, dateTo);
		if (japanStocks == null || japanStocks.size() == 0) {
			return null;
		} else {
			// Generate japan stock key.
			JapanStockKey japanStockKey = new JapanStockKey();
			japanStockKey.setCode(code);
			japanStockKey.setRegistDate(dateFrom);
			// new TrnJapanStockWeekly instance.
			TrnJapanStockWeekly japanStockWeekly = new TrnJapanStockWeekly();
			japanStockWeekly.setJapanStockKey(japanStockKey);
			Integer highPrice = 0;
			Integer lowPrice = 0;
			Long turnover = 0L;
			Long tradingValue = 0L;
			for(int i = 0; i < japanStocks.size(); i++) {
				TrnJapanStock japanStock = japanStocks.get(i);
				// Set opening price.
				if (i == 0) {
					japanStockWeekly.setOpeningPrice(japanStock.getOpeningPrice());
				}
				// Get high price.
				if (highPrice < japanStock.getHighPrice()) {
					highPrice = japanStock.getHighPrice();
				}
				// Get low price.
				if (lowPrice == 0  || lowPrice > japanStock.getLowPrice()) {
					lowPrice = japanStock.getLowPrice();
				}
				// Set finish price.
				if (i == japanStocks.size() - 1) {
					japanStockWeekly.setFinishPrice(japanStock.getFinishPrice());
				}
				// Sum turnover.
				turnover = turnover + japanStock.getTurnover();
				// Sum tradingValue.
				tradingValue = tradingValue + japanStock.getTradingValue();
			}
			// Set high price.
			japanStockWeekly.setHighPrice(highPrice);
			// Set low price.
			japanStockWeekly.setLowPrice(lowPrice);
			// Set turnover.
			japanStockWeekly.setTurnover(turnover);
			// Set tradingValue.
			japanStockWeekly.setTradingValue(tradingValue);
			return japanStockWeekly;
		}
	}
}
