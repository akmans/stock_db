package com.akmans.trade.fx.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akmans.trade.core.enums.FXType;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.fx.service.FXTickService;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXTickRepository;

@Service
public class FXTickServiceImpl implements FXTickService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXTickServiceImpl.class);

	private MessageService messageService;

	private TrnFXTickRepository trnFXTickRepository;

	@Autowired
	FXTickServiceImpl(TrnFXTickRepository trnFXTickRepository, MessageService messageService) {
		this.trnFXTickRepository = trnFXTickRepository;
		this.messageService = messageService;
	}

	@Transactional
	public TrnFXTick operation(TrnFXTick tick, OperationMode mode) throws TradeException {
		logger.debug("the tick is {}", tick);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			return trnFXTickRepository.save(tick);
		}
		case EDIT: {
			Optional<TrnFXTick> origin = trnFXTickRepository.findOne(tick.getCode());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent", tick.getCode()));
			}
			return trnFXTickRepository.save(tick);
		}
		case DELETE: {
			Optional<TrnFXTick> origin = trnFXTickRepository.findOne(tick.getCode());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent", tick.getCode()));
			}
			trnFXTickRepository.delete(tick);
		}
		}
		return null;
	}

	public Optional<TrnFXTick> findOne(Long code) throws TradeException {
		return trnFXTickRepository.findOne(code);
	}

	public AbstractFXEntity generateFXPeriodData(FXType type, String currencyPair, LocalDateTime dateTimeFrom) {
		LocalDateTime dateTimeTo = null;
		switch (type) {
		case HOUR: {
			dateTimeTo = dateTimeFrom.plusHours(1);
			break;
		}
		case SIXHOUR: {
			dateTimeTo = dateTimeFrom.plusHours(6);
			break;
		}
		case DAY: {
			dateTimeTo = dateTimeFrom.plusDays(1);
			break;
		}
		case WEEK: {
			dateTimeTo = dateTimeFrom.plusWeeks(1);
			break;
		}
		case MONTH: {
			dateTimeTo = dateTimeFrom.plusMonths(1);
			break;
		}
		}
		logger.debug("Currency pair is {}.", currencyPair);
		logger.debug("Date from is {}.", dateTimeFrom);
		logger.debug("Date to is {}.", dateTimeTo);
		if (trnFXTickRepository.countFXTickInPeriod(currencyPair, dateTimeFrom, dateTimeTo) <= 0) {
			logger.debug("No records found.");
			return null;
		} else {
			// Generate tick key.
			FXTickKey tickKey = new FXTickKey();
			tickKey.setCurrencyPair(currencyPair);
			tickKey.setRegistDate(dateTimeFrom);
			// new TrnFX... instance.
			AbstractFXEntity fxEntity = null;
			switch (type) {
			case HOUR: {
				fxEntity = new TrnFXHour();
				break;
			}
			case SIXHOUR: {
				fxEntity = new TrnFX6Hour();
				break;
			}
			case DAY: {
				fxEntity = new TrnFXDay();
				break;
			}
			case WEEK: {
				fxEntity = new TrnFXWeek();
				break;
			}
			case MONTH: {
				fxEntity = new TrnFXMonth();
				break;
			}
			}
			// Set Key.
			fxEntity.setTickKey(tickKey);
			// Set opening price.
			List<TrnFXTick> ticks = trnFXTickRepository.findFirstFXTickInPeriod(currencyPair, dateTimeFrom, dateTimeTo);
			fxEntity.setOpeningPrice(ticks.get(0).getMidPrice());
			// Set high price.
			ticks = trnFXTickRepository.findHighestFXTickInPeriod(currencyPair, dateTimeFrom, dateTimeTo);
			fxEntity.setHighPrice(ticks.get(0).getMidPrice());
			// Set low price.
			ticks = trnFXTickRepository.findLowestFXTickInPeriod(currencyPair, dateTimeFrom, dateTimeTo);
			fxEntity.setLowPrice(ticks.get(0).getMidPrice());
			// Set finish price.
			ticks = trnFXTickRepository.findLastFXTickInPeriod(currencyPair, dateTimeFrom, dateTimeTo);
			fxEntity.setFinishPrice(ticks.get(0).getMidPrice());
			return fxEntity;
		}
	}
}
