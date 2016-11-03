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
import com.akmans.trade.fx.service.FXTickBulkService;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTickBulk;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXTickBulkRepository;

@Service
public class FXTickBulkServiceImpl implements FXTickBulkService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXTickBulkServiceImpl.class);

	private MessageService messageService;

	private TrnFXTickBulkRepository trnFXTickBulkRepository;

	@Autowired
	FXTickBulkServiceImpl(TrnFXTickBulkRepository trnFXTickBulkRepository, MessageService messageService) {
		this.trnFXTickBulkRepository = trnFXTickBulkRepository;
		this.messageService = messageService;
	}

	@Transactional
	public TrnFXTickBulk operation(TrnFXTickBulk tick, OperationMode mode) throws TradeException {
		logger.debug("the tick is {}", tick);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			return trnFXTickBulkRepository.save(tick);
		}
		case EDIT: {
			Optional<TrnFXTickBulk> origin = trnFXTickBulkRepository.findOne(tick.getCode());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent", tick.getCode()));
			}
			return trnFXTickBulkRepository.save(tick);
		}
		case DELETE: {
			Optional<TrnFXTickBulk> origin = trnFXTickBulkRepository.findOne(tick.getCode());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent", tick.getCode()));
			}
			trnFXTickBulkRepository.delete(tick);
		}
		}
		return null;
	}

	public Optional<TrnFXTickBulk> findOne(Long code) throws TradeException {
		return trnFXTickBulkRepository.findOne(code);
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
		if (trnFXTickBulkRepository.countFXTickInPeriod(currencyPair, dateTimeFrom, dateTimeTo) <= 0) {
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
			List<TrnFXTickBulk> ticks = trnFXTickBulkRepository.findFirstFXTickInPeriod(currencyPair, dateTimeFrom, dateTimeTo);
			fxEntity.setOpeningPrice(ticks.get(0).getMidPrice());
			// Set high price.
			ticks = trnFXTickBulkRepository.findHighestFXTickInPeriod(currencyPair, dateTimeFrom, dateTimeTo);
			fxEntity.setHighPrice(ticks.get(0).getMidPrice());
			// Set low price.
			ticks = trnFXTickBulkRepository.findLowestFXTickInPeriod(currencyPair, dateTimeFrom, dateTimeTo);
			fxEntity.setLowPrice(ticks.get(0).getMidPrice());
			// Set finish price.
			ticks = trnFXTickBulkRepository.findLastFXTickInPeriod(currencyPair, dateTimeFrom, dateTimeTo);
			fxEntity.setFinishPrice(ticks.get(0).getMidPrice());
			return fxEntity;
		}
	}
}
