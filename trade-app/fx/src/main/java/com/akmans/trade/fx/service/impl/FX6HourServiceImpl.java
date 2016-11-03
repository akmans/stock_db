package com.akmans.trade.fx.service.impl;

import java.time.LocalDateTime;
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
import com.akmans.trade.fx.service.FX6HourService;
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFX6HourRepository;

@Service
public class FX6HourServiceImpl implements FX6HourService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FX6HourServiceImpl.class);

	private FXHourService fxHourService;

	private MessageService messageService;

	private TrnFX6HourRepository trnFX6HourRepository;

	@Autowired
	FX6HourServiceImpl(FXHourService fxHourService, TrnFX6HourRepository trnFX6HourRepository, MessageService messageService) {
		this.fxHourService = fxHourService;
		this.trnFX6HourRepository = trnFX6HourRepository;
		this.messageService = messageService;
	}

	@Transactional
	public TrnFX6Hour operation(TrnFX6Hour tick, OperationMode mode) throws TradeException {
		logger.debug("the tick is {}", tick);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			Optional<TrnFX6Hour> result = trnFX6HourRepository.findOne(tick.getTickKey());
			if (result.isPresent()) {
				throw new TradeException(messageService.getMessage("core.service.record.alreadyexist", tick.getTickKey()));
			}
			return trnFX6HourRepository.save(tick);
		}
		case EDIT: {
			Optional<TrnFX6Hour> origin = trnFX6HourRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent", tick.getTickKey()));
			}
			return trnFX6HourRepository.save(tick);
		}
		case DELETE: {
			Optional<TrnFX6Hour> origin = trnFX6HourRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent", tick.getTickKey()));
			}
			trnFX6HourRepository.delete(tick);
		}
		}
		return null;
	}

	public Optional<TrnFX6Hour> findOne(FXTickKey key) {
		return trnFX6HourRepository.findOne(key);
	}

	public Optional<TrnFX6Hour> findPrevious(FXTickKey key) {
		if (key == null) {
			return Optional.empty();
		}
		return trnFX6HourRepository.findPrevious(key.getCurrencyPair(), key.getRegistDate());
	}

	@Transactional
	public OperationResult refresh(String currencyPair, LocalDateTime currentDatetime) throws TradeException {
		// The return value.
		OperationResult rtn = null;
		// Retrieve weekly data.
		TrnFX6Hour fx6Hour = (TrnFX6Hour)fxHourService
				.generateFXPeriodData(FXType.SIXHOUR, currencyPair, currentDatetime);
		// Continue to next instrument if no weekly data found.
		if (fx6Hour != null) {
			// Retrieve previous FX Hour data from DB by current key.
			Optional<TrnFX6Hour> prevOption = this.findPrevious(fx6Hour.getTickKey());
			// If exists.
			if (prevOption.isPresent()) {
				TrnFX6Hour previous = prevOption.get();
				fx6Hour.setAvOpeningPrice((previous.getAvOpeningPrice() + previous.getAvFinishPrice()) / 2);
				fx6Hour.setAvFinishPrice((fx6Hour.getOpeningPrice() + fx6Hour.getHighPrice() + fx6Hour.getLowPrice()
						+ fx6Hour.getFinishPrice()) / 4);
			} else {
				fx6Hour.setAvOpeningPrice(fx6Hour.getOpeningPrice());
				fx6Hour.setAvFinishPrice(fx6Hour.getFinishPrice());
			}
			// Retrieve FX 6Hour data from DB by key.
			Optional<TrnFX6Hour> option = this.findOne(fx6Hour.getTickKey());
			// Do delete & insert if exist, or else do insert.
			if (option.isPresent()) {
				// Get the current data.
				TrnFX6Hour current = option.get();
				// Adapt all prices data.
				current.setOpeningPrice(fx6Hour.getOpeningPrice());
				current.setHighPrice(fx6Hour.getHighPrice());
				current.setLowPrice(fx6Hour.getLowPrice());
				current.setFinishPrice(fx6Hour.getFinishPrice());
				current.setAvOpeningPrice(fx6Hour.getAvOpeningPrice());
				current.setAvFinishPrice(fx6Hour.getAvFinishPrice());
				// Copy all data.
				BeanUtils.copyProperties(current, fx6Hour);
				logger.debug("The updated data is {}", fx6Hour);
				// Delete the current weekly data.
				this.operation(current, OperationMode.DELETE);
				// Insert a new weekly data.
				this.operation(fx6Hour, OperationMode.NEW);
				// Mark operation as update.
				rtn = OperationResult.UPDATED;
			} else {
				logger.debug("The inserted data is {}", fx6Hour);
				// Insert a new weekly data.
				this.operation(fx6Hour, OperationMode.NEW);
				// Mark operation as insert.
				rtn = OperationResult.INSERTED;
			}
		} else {
			logger.info("No FX 6Hour generated at {}", currentDatetime);
			// Mark operation as nothing.
			rtn = OperationResult.NONE;
		}
		return rtn;
	}
}
