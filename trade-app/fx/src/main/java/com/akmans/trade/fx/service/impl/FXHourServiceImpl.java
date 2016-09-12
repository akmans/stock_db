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
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXHourRepository;

@Service
public class FXHourServiceImpl implements FXHourService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXHourServiceImpl.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private TrnFXHourRepository trnFXHourRepository;

	@Autowired
	AuditorAware<String> auditor;

	public void operation(TrnFXHour tick, OperationMode mode) throws TradeException {
		logger.debug("the tick is {}", tick);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			Optional<TrnFXHour> result = trnFXHourRepository.findOne(tick.getTickKey());
			if (result.isPresent()) {
				throw new TradeException(messageService.getMessage("TODO", tick.getTickKey()));
			}
			trnFXHourRepository.save(tick);
			break;
		}
		case EDIT: {
			Optional<TrnFXHour> origin = trnFXHourRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("TODO", tick.getTickKey()));
			}
			trnFXHourRepository.save(tick);
			break;
		}
		case DELETE: {
			Optional<TrnFXHour> origin = trnFXHourRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("TODO", tick.getTickKey()));
			}
			trnFXHourRepository.delete(tick);
		}
		}
	}

	public Optional<TrnFXHour> findOne(FXTickKey key) {
		return trnFXHourRepository.findOne(key);
	}

	public AbstractFXEntity generateFXPeriodData(FXType type, String currencyPair, ZonedDateTime dateTimeFrom) {
		ZonedDateTime dateTimeTo = null;
		switch (type) {
		case HOUR: {
			return null;
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
		List<TrnFXHour> fxHours = trnFXHourRepository.findFXHourInPeriod(currencyPair, dateTimeFrom, dateTimeTo);
		if (fxHours == null || fxHours.size() == 0) {
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
				return null;
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
			double highPrice = 0;
			double lowPrice = 0;
			for (int i = 0; i < fxHours.size(); i++) {
				TrnFXHour hour = fxHours.get(i);
				logger.debug("the TrnFXHour is {}", hour);
				// Set opening price, the first tick price is opening price.
				if (i == 0) {
					fxEntity.setOpeningPrice(hour.getOpeningPrice());
				}
				// Refresh high price when necessary.
				if (highPrice < hour.getHighPrice()) {
					highPrice = hour.getHighPrice();
				}
				// Get low price when necessary.(MUST: refresh lowPrice when is 0.)
				if (lowPrice == 0 || lowPrice > hour.getLowPrice()) {
					lowPrice = hour.getLowPrice();
				}
				// Set finish price.
				if (i == fxHours.size() - 1) {
					fxEntity.setFinishPrice(hour.getFinishPrice());
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
