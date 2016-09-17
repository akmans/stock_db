package com.akmans.trade.fx.service.impl;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.FXType;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.fx.service.FXDayService;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXDayRepository;

@Service
public class FXDayServiceImpl implements FXDayService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXDayServiceImpl.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private TrnFXDayRepository trnFXDayRepository;

	@Autowired
	AuditorAware<String> auditor;

	public void operation(TrnFXDay tick, OperationMode mode) throws TradeException {
		logger.debug("the tick is {}", tick);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			Optional<TrnFXDay> result = trnFXDayRepository.findOne(tick.getTickKey());
			if (result.isPresent()) {
				throw new TradeException(messageService.getMessage("TODO", tick.getTickKey()));
			}
			trnFXDayRepository.save(tick);
			break;
		}
		case EDIT: {
			Optional<TrnFXDay> origin = trnFXDayRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("TODO", tick.getTickKey()));
			}
			trnFXDayRepository.save(tick);
			break;
		}
		case DELETE: {
			Optional<TrnFXDay> origin = trnFXDayRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("TODO", tick.getTickKey()));
			}
			trnFXDayRepository.delete(tick);
		}
		}
	}

	public Optional<TrnFXDay> findOne(FXTickKey key) {
		return trnFXDayRepository.findOne(key);
	}

	public Optional<TrnFXDay> findPrevious(FXTickKey key) {
		if (key == null) {
			return Optional.empty();
		}
		return trnFXDayRepository.findPrevious(key.getCurrencyPair(), key.getRegistDate());
	}

	public AbstractFXEntity generateFXPeriodData(FXType type, String currencyPair, ZonedDateTime dateTimeFrom) {
		ZonedDateTime dateTimeTo = null;
		switch (type) {
		case HOUR: case SIXHOUR: case DAY: {
			return null;
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
		List<TrnFXDay> fxDays = trnFXDayRepository.findFXDayInPeriod(currencyPair, dateTimeFrom, dateTimeTo);
		if (fxDays == null || fxDays.size() == 0) {
			return null;
		} else {
			// Generate tick key.
			FXTickKey tickKey = new FXTickKey();
			tickKey.setCurrencyPair(currencyPair);
			tickKey.setRegistDate(dateTimeFrom);
			// new TrnFX... instance.
			AbstractFXEntity fxEntity = null;
			switch (type) {
			case HOUR: case SIXHOUR: case DAY: {
				return null;
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
			double highPrice = 0;
			double lowPrice = 0;
			for (int i = 0; i < fxDays.size(); i++) {
				TrnFXDay day = fxDays.get(i);
				// Set opening price, the first tick price is opening price.
				if (i == 0) {
					fxEntity.setOpeningPrice(day.getOpeningPrice());
				}
				// Refresh high price when necessary.
				if (highPrice < day.getHighPrice()) {
					highPrice = day.getHighPrice();
				}
				// Get low price when necessary.
				if (lowPrice == 0 || lowPrice > day.getLowPrice()) {
					lowPrice = day.getLowPrice();
				}
				// Set finish price.
				if (i == fxDays.size() - 1) {
					fxEntity.setFinishPrice(day.getFinishPrice());
				}
			}
			// Set high price.
			fxEntity.setHighPrice(highPrice);
			// Set low price.
			fxEntity.setLowPrice(lowPrice);
			return fxEntity;
		}
	}
}
