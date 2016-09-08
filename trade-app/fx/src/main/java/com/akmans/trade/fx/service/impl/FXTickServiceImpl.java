package com.akmans.trade.fx.service.impl;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.fx.service.FXTickService;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXTickRepository;

@Service
public class FXTickServiceImpl<T extends AbstractFXEntity> implements FXTickService<AbstractFXEntity> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXTickServiceImpl.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private TrnFXTickRepository trnFXTickRepository;

	public void operation(TrnFXTick tick, OperationMode mode) throws TradeException {
		logger.debug("the tick is {}", tick);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			Optional<TrnFXTick> result = trnFXTickRepository.findOne(tick.getTickKey());
			if (result.isPresent()) {
				throw new TradeException(
						messageService.getMessage("TODO", tick.getTickKey()));
			}
			trnFXTickRepository.save(tick);
			break;
		}
		case EDIT: {
			Optional<TrnFXTick> origin = trnFXTickRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(
						messageService.getMessage("TODO", tick.getTickKey()));
			}
			trnFXTickRepository.save(tick);
			break;
		}
		case DELETE: {
			Optional<TrnFXTick> origin = trnFXTickRepository.findOne(tick.getTickKey());
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(tick.getUpdatedDate())) {
				throw new TradeException(
						messageService.getMessage("TODO", tick.getTickKey()));
			}
			trnFXTickRepository.delete(tick);
		}
		}
	}

	public TrnFXTick findOne(FXTickKey key) throws TradeException {
		Optional<TrnFXTick> tick = trnFXTickRepository.findOne(key);
		if (tick.isPresent()) {
			return tick.get();
		} else {
			throw new TradeException(
					messageService.getMessage("TODO", key.toString()));
		}
	}

	public boolean exist(FXTickKey key) {
		return trnFXTickRepository.findOne(key).isPresent();
	}

	public T generateFXPeriodData(String currencyPair, ZonedDateTime dateTimeFrom, ZonedDateTime dateTimeTo) {
		return null;
	};
}
