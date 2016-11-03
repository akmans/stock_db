package com.akmans.trade.fx.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akmans.trade.core.enums.FXType;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.enums.OperationResult;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.fx.service.FXDayService;
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXDayRepository;

@Service
public class FXDayServiceImpl implements FXDayService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXDayServiceImpl.class);

	private FXHourService fxHourService;

	private MessageService messageService;

	private TrnFXDayRepository trnFXDayRepository;

	@Autowired
	FXDayServiceImpl(FXHourService fxHourService, TrnFXDayRepository trnFXDayRepository, MessageService messageService) {
		this.fxHourService = fxHourService;
		this.trnFXDayRepository = trnFXDayRepository;
		this.messageService = messageService;
	}

	@Transactional
	public TrnFXDay operation(TrnFXDay tick, OperationMode mode) throws TradeException {
		logger.debug("the tick is {}", tick);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			Optional<TrnFXDay> result = trnFXDayRepository.findOne(tick.getTickKey());
			if (result.isPresent()) {
				throw new TradeException(messageService.getMessage("core.service.record.alreadyexist", tick.getTickKey()));
			}
			return trnFXDayRepository.save(tick);
		}
		case EDIT: {
			Optional<TrnFXDay> origin = trnFXDayRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent", tick.getTickKey()));
			}
			return trnFXDayRepository.save(tick);
		}
		case DELETE: {
			Optional<TrnFXDay> origin = trnFXDayRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent", tick.getTickKey()));
			}
			trnFXDayRepository.delete(tick);
		}
		}
		return null;
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

	public AbstractFXEntity generateFXPeriodData(FXType type, String currencyPair, LocalDateTime dateTimeFrom) {
		LocalDateTime dateTimeTo = null;
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

	@Transactional
	public OperationResult refresh(String currencyPair, LocalDateTime currentDatetime) throws TradeException {
		// The return value.
		OperationResult rtn = null;
		// Retrieve daily data.
		TrnFXDay fxDay = (TrnFXDay)fxHourService
				.generateFXPeriodData(FXType.DAY, currencyPair, currentDatetime);
		// Continue to next instrument if no weekly data found.
		if (fxDay != null) {
			// Retrieve previous FX Day data from DB by current key.
			Optional<TrnFXDay> prevOption = this.findPrevious(fxDay.getTickKey());
			// If exists.
			if (prevOption.isPresent()) {
				TrnFXDay previous = prevOption.get();
				fxDay.setAvOpeningPrice((previous.getAvOpeningPrice() + previous.getAvFinishPrice()) / 2);
				fxDay.setAvFinishPrice((fxDay.getOpeningPrice() + fxDay.getHighPrice() + fxDay.getLowPrice()
						+ fxDay.getFinishPrice()) / 4);
			} else {
				fxDay.setAvOpeningPrice(fxDay.getOpeningPrice());
				fxDay.setAvFinishPrice(fxDay.getFinishPrice());
			}
			// Retrieve FX Day data from DB by key.
			Optional<TrnFXDay> option = this.findOne(fxDay.getTickKey());
			// Do delete & insert if exist, or else do insert.
			if (option.isPresent()) {
				// Get the current data.
				TrnFXDay current = option.get();
				// Adapt all prices data.
				current.setOpeningPrice(fxDay.getOpeningPrice());
				current.setHighPrice(fxDay.getHighPrice());
				current.setLowPrice(fxDay.getLowPrice());
				current.setFinishPrice(fxDay.getFinishPrice());
				current.setAvOpeningPrice(fxDay.getAvOpeningPrice());
				current.setAvFinishPrice(fxDay.getAvFinishPrice());
				// Copy all data.
				BeanUtils.copyProperties(current, fxDay);
				logger.debug("The updated data is {}", fxDay);
				// Delete the current weekly data.
				this.operation(current, OperationMode.DELETE);
				// Insert a new weekly data.
				this.operation(fxDay, OperationMode.NEW);
				// Mark operation as update.
				rtn = OperationResult.UPDATED;
			} else {
				logger.debug("The inserted data is {}", fxDay);
				// Insert a new weekly data.
				this.operation(fxDay, OperationMode.NEW);
				// Mark operation as insert.
				rtn = OperationResult.INSERTED;
			}
		} else {
			logger.info("No FX Day generated at {}", currentDatetime);
			// Mark operation as nothing.
			rtn = OperationResult.NONE;
		}
		return rtn;
	}
}
