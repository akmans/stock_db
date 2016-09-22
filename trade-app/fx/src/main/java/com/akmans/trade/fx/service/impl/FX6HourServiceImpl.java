package com.akmans.trade.fx.service.impl;

import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.fx.service.FX6HourService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFX6HourRepository;

@Service
public class FX6HourServiceImpl implements FX6HourService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FX6HourServiceImpl.class);

	private final MessageService messageService;

	private final TrnFX6HourRepository trnFX6HourRepository;

	@Autowired
	FX6HourServiceImpl(TrnFX6HourRepository trnFX6HourRepository, MessageService messageService) {
		this.trnFX6HourRepository = trnFX6HourRepository;
		this.messageService = messageService;
	}

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
}
