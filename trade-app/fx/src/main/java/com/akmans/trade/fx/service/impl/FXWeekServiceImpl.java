package com.akmans.trade.fx.service.impl;

import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.fx.service.FXWeekService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXWeekRepository;

@Service
public class FXWeekServiceImpl implements FXWeekService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXWeekServiceImpl.class);

	private MessageService messageService;

	private TrnFXWeekRepository trnFXWeekRepository;

	@Autowired
	FXWeekServiceImpl(TrnFXWeekRepository trnFXWeekRepository, MessageService messageService) {
		this.trnFXWeekRepository = trnFXWeekRepository;
		this.messageService = messageService;
	}

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
}
