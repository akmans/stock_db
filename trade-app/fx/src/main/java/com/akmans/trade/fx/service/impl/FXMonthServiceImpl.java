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
import com.akmans.trade.fx.service.FXMonthService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXMonthRepository;

@Service
public class FXMonthServiceImpl implements FXMonthService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXMonthServiceImpl.class);

	private FXDayService fxDayService;

	private MessageService messageService;

	private TrnFXMonthRepository trnFXMonthRepository;

	@Autowired
	FXMonthServiceImpl(FXDayService fxDayService, TrnFXMonthRepository trnFXMonthRepository, MessageService messageService) {
		this.fxDayService = fxDayService;
		this.trnFXMonthRepository = trnFXMonthRepository;
		this.messageService = messageService;
	}

	@Transactional
	public TrnFXMonth operation(TrnFXMonth tick, OperationMode mode) throws TradeException {
		logger.debug("the tick is {}", tick);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			Optional<TrnFXMonth> result = trnFXMonthRepository.findOne(tick.getTickKey());
			if (result.isPresent()) {
				throw new TradeException(messageService.getMessage("core.service.record.alreadyexist", tick.getTickKey()));
			}
			return trnFXMonthRepository.save(tick);
		}
		case EDIT: {
			Optional<TrnFXMonth> origin = trnFXMonthRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent", tick.getTickKey()));
			}
			return trnFXMonthRepository.save(tick);
		}
		case DELETE: {
			Optional<TrnFXMonth> origin = trnFXMonthRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent", tick.getTickKey()));
			}
			trnFXMonthRepository.delete(tick);
		}
		}
		return null;
	}

	public Optional<TrnFXMonth> findOne(FXTickKey key) {
		return trnFXMonthRepository.findOne(key);
	}

	public Optional<TrnFXMonth> findPrevious(FXTickKey key) {
		if (key == null) {
			return Optional.empty();
		}
		return trnFXMonthRepository.findPrevious(key.getCurrencyPair(), key.getRegistDate());
	}

	@Transactional
	public OperationResult refresh(String currencyPair, LocalDateTime currentDatetime) throws TradeException {
		// The return value.
		OperationResult rtn = null;
		// Retrieve weekly data.
		TrnFXMonth fxMonth = (TrnFXMonth)fxDayService
				.generateFXPeriodData(FXType.MONTH, currencyPair, currentDatetime);
		// Continue to next instrument if no weekly data found.
		if (fxMonth != null) {
			// Retrieve previous FX Month data from DB by current key.
			Optional<TrnFXMonth> prevOption = this.findPrevious(fxMonth.getTickKey());
			// If exists.
			if (prevOption.isPresent()) {
				TrnFXMonth previous = prevOption.get();
				fxMonth.setAvOpeningPrice((previous.getAvOpeningPrice() + previous.getAvFinishPrice()) / 2);
				fxMonth.setAvFinishPrice((fxMonth.getOpeningPrice() + fxMonth.getHighPrice() + fxMonth.getLowPrice()
						+ fxMonth.getFinishPrice()) / 4);
			} else {
				fxMonth.setAvOpeningPrice(fxMonth.getOpeningPrice());
				fxMonth.setAvFinishPrice(fxMonth.getFinishPrice());
			}
			// Retrieve FX Month data from DB by key.
			Optional<TrnFXMonth> option = this.findOne(fxMonth.getTickKey());
			// Do delete & insert if exist, or else do insert.
			if (option.isPresent()) {
				// Get the current data.
				TrnFXMonth current = option.get();
				// Adapt all prices data.
				current.setOpeningPrice(fxMonth.getOpeningPrice());
				current.setHighPrice(fxMonth.getHighPrice());
				current.setLowPrice(fxMonth.getLowPrice());
				current.setFinishPrice(fxMonth.getFinishPrice());
				current.setAvOpeningPrice(fxMonth.getAvOpeningPrice());
				current.setAvFinishPrice(fxMonth.getAvFinishPrice());
				// Copy all data.
				BeanUtils.copyProperties(current, fxMonth);
				logger.debug("The updated data is {}", fxMonth);
				// Delete the current weekly data.
				this.operation(current, OperationMode.DELETE);
				// Insert a new weekly data.
				this.operation(fxMonth, OperationMode.NEW);
				// Mark operation as update.
				rtn = OperationResult.UPDATED;
			} else {
				logger.debug("The inserted data is {}", fxMonth);
				// Insert a new weekly data.
				this.operation(fxMonth, OperationMode.NEW);
				// Mark operation as insert.
				rtn = OperationResult.INSERTED;
			}
		} else {
			logger.info("No FX Month generated at {}", currentDatetime);
			// Mark operation as nothing.
			rtn = OperationResult.NONE;
		}
		return rtn;
	}
}
