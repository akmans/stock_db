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
import com.akmans.trade.fx.service.FXDayService;
import com.akmans.trade.fx.service.FXWeekService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXWeekRepository;

@Service
public class FXWeekServiceImpl implements FXWeekService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXWeekServiceImpl.class);

	private FXDayService fxDayService;

	private MessageService messageService;

	private TrnFXWeekRepository trnFXWeekRepository;

	@Autowired
	FXWeekServiceImpl(FXDayService fxDayService, TrnFXWeekRepository trnFXWeekRepository, MessageService messageService) {
		this.fxDayService = fxDayService;
		this.trnFXWeekRepository = trnFXWeekRepository;
		this.messageService = messageService;
	}

	@Transactional
	public TrnFXWeek operation(TrnFXWeek tick, OperationMode mode) throws TradeException {
		logger.debug("the tick is {}", tick);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			Optional<TrnFXWeek> result = trnFXWeekRepository.findOne(tick.getTickKey());
			if (result.isPresent()) {
				throw new TradeException(messageService.getMessage("core.service.record.alreadyexist", tick.getTickKey()));
			}
			return trnFXWeekRepository.save(tick);
		}
		case EDIT: {
			Optional<TrnFXWeek> origin = trnFXWeekRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent", tick.getTickKey()));
			}
			return trnFXWeekRepository.save(tick);
		}
		case DELETE: {
			Optional<TrnFXWeek> origin = trnFXWeekRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent", tick.getTickKey()));
			}
			trnFXWeekRepository.delete(tick);
		}
		}
		return null;
	}

	public Optional<TrnFXWeek> findOne(FXTickKey key) {
		return trnFXWeekRepository.findOne(key);
	}

	public Optional<TrnFXWeek> findPrevious(FXTickKey key) {
		if (key == null) {
			return Optional.empty();
		}
		return trnFXWeekRepository.findPrevious(key.getCurrencyPair(), key.getRegistDate());
	}

	@Transactional
	public OperationResult refresh(String currencyPair, LocalDateTime currentDatetime) throws TradeException {
		// The return value.
		OperationResult rtn = null;
		// Retrieve weekly data.
		TrnFXWeek fxWeek = (TrnFXWeek)fxDayService
				.generateFXPeriodData(FXType.WEEK, currencyPair, currentDatetime);
		// Continue to next instrument if no weekly data found.
		if (fxWeek != null) {
			// Retrieve previous FX Week data from DB by current key.
			Optional<TrnFXWeek> prevOption = this.findPrevious(fxWeek.getTickKey());
			// If exists.
			if (prevOption.isPresent()) {
				TrnFXWeek previous = prevOption.get();
				fxWeek.setAvOpeningPrice((previous.getAvOpeningPrice() + previous.getAvFinishPrice()) / 2);
				fxWeek.setAvFinishPrice((fxWeek.getOpeningPrice() + fxWeek.getHighPrice() + fxWeek.getLowPrice()
						+ fxWeek.getFinishPrice()) / 4);
			} else {
				fxWeek.setAvOpeningPrice(fxWeek.getOpeningPrice());
				fxWeek.setAvFinishPrice(fxWeek.getFinishPrice());
			}
			// Retrieve FX Week data from DB by key.
			Optional<TrnFXWeek> option = this.findOne(fxWeek.getTickKey());
			// Do delete & insert if exist, or else do insert.
			if (option.isPresent()) {
				// Get the current data.
				TrnFXWeek current = option.get();
				// Adapt all prices data.
				current.setOpeningPrice(fxWeek.getOpeningPrice());
				current.setHighPrice(fxWeek.getHighPrice());
				current.setLowPrice(fxWeek.getLowPrice());
				current.setFinishPrice(fxWeek.getFinishPrice());
				current.setAvOpeningPrice(fxWeek.getAvOpeningPrice());
				current.setAvFinishPrice(fxWeek.getAvFinishPrice());
				// Copy all data.
				BeanUtils.copyProperties(current, fxWeek);
				logger.debug("The updated data is {}", fxWeek);
				// Delete the current weekly data.
				this.operation(current, OperationMode.DELETE);
				// Insert a new weekly data.
				this.operation(fxWeek, OperationMode.NEW);
				// Mark operation as update.
				rtn = OperationResult.UPDATED;
			} else {
				logger.debug("The inserted data is {}", fxWeek);
				// Insert a new weekly data.
				this.operation(fxWeek, OperationMode.NEW);
				// Mark operation as insert.
				rtn = OperationResult.INSERTED;
			}
		} else {
			logger.info("No FX Week generated at {}", currentDatetime);
			// Mark operation as nothing.
			rtn = OperationResult.NONE;
		}
		return rtn;
	}
}
