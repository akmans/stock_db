package com.akmans.trade.cashing.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.akmans.trade.cashing.service.SpecialItemService;
import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.cashing.springdata.jpa.repositories.TrnSpecialItemRepository;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;

@Service
public class SpecialItemServiceImpl implements SpecialItemService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SpecialItemServiceImpl.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private TrnSpecialItemRepository trnSpecialItemRepository;

	public Page<TrnSpecialItem> findAll(Pageable pageable) {
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		return trnSpecialItemRepository.findAll(pg);
	}

	public List<TrnSpecialItem> findAll() {
		return trnSpecialItemRepository.findAllByOrderByCodeAsc();
	}

	public TrnSpecialItem findOne(Integer code) throws TradeException {
		Optional<TrnSpecialItem> specialItem = trnSpecialItemRepository.findOne(code);
		if (specialItem.isPresent()) {
			return specialItem.get();
		} else {
			throw new TradeException(messageService.getMessage("core.service.specialitem.record.notfound", code));
		}
	}

	public void operation(TrnSpecialItem specialItem, OperationMode mode) throws TradeException {
		logger.debug("the specialItem is {}", specialItem);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			trnSpecialItemRepository.save(specialItem);
			break;
		}
		case EDIT: {
			TrnSpecialItem origin = findOne(specialItem.getCode());
			if (!origin.getUpdatedDate().equals(specialItem.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.specialitem.record.inconsistent",
						specialItem.getCode()));
			}
			trnSpecialItemRepository.save(specialItem);
			break;
		}
		case DELETE: {
			TrnSpecialItem origin = findOne(specialItem.getCode());
			if (!origin.getUpdatedDate().equals(specialItem.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.specialitem.record.inconsistent",
						specialItem.getCode()));
			}
			trnSpecialItemRepository.delete(specialItem);
		}
		}
	}
}
