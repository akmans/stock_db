package com.akmans.trade.fx.service.impl;

import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.fx.service.FXMonthService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXMonthRepository;

@Service
public class FXMonthServiceImpl implements FXMonthService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXMonthServiceImpl.class);

	private MessageService messageService;

	private TrnFXMonthRepository trnFXMonthRepository;

	@Autowired
	FXMonthServiceImpl(TrnFXMonthRepository trnFXMonthRepository, MessageService messageService) {
		this.trnFXMonthRepository = trnFXMonthRepository;
		this.messageService = messageService;
	}

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
}
