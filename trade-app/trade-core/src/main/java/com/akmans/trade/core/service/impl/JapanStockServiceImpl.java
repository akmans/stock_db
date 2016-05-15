package com.akmans.trade.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.dto.CalendarQueryDto;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.CalendarService;
import com.akmans.trade.core.service.JapanStockService;
import com.akmans.trade.core.springdata.jpa.repositories.MstCalendarRepository;
import com.akmans.trade.core.springdata.jpa.repositories.TrnJapanStockRepository;
import com.akmans.trade.core.springdata.jpa.entities.MstCalendar;
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
