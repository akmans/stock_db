package com.akmans.trade.stock.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.stock.service.ScaleService;
import com.akmans.trade.stock.springdata.jpa.repositories.MstScaleRepository;
import com.akmans.trade.stock.springdata.jpa.entities.MstScale;

@Service
public class ScaleServiceImpl implements ScaleService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ScaleServiceImpl.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private MstScaleRepository mstScaleRepository;

	public List<MstScale> findAll() {
		return mstScaleRepository.findAllByOrderByCodeAsc();
	}

	public Page<MstScale> findAll(Pageable pageable) {
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		return mstScaleRepository.findAll(pg);
	}

	public MstScale findOne(Integer code) throws TradeException {
		Optional<MstScale> scale = mstScaleRepository.findOne(code);
		if (scale.isPresent()) {
			return scale.get();
		} else {
			throw new TradeException(messageService.getMessage("core.service.scale.record.notfound", code));
		}
	}

	public void operation(MstScale scale, OperationMode mode) throws TradeException {
		logger.debug("the scale is {}", scale);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			if (mstScaleRepository.findOne(scale.getCode()).isPresent()) {
				throw new TradeException(
						messageService.getMessage("core.service.market.record.alreadyexist", scale.getCode()));
			}
			mstScaleRepository.save(scale);
			break;
		}
		case EDIT: {
			MstScale origin = findOne(scale.getCode());
			if (!origin.getUpdatedDate().equals(scale.getUpdatedDate())) {
				throw new TradeException(
						messageService.getMessage("core.service.market.record.inconsistent", scale.getCode()));
			}
			mstScaleRepository.save(scale);
			break;
		}
		case DELETE: {
			MstScale origin = findOne(scale.getCode());
			if (!origin.getUpdatedDate().equals(scale.getUpdatedDate())) {
				throw new TradeException(
						messageService.getMessage("core.service.market.record.inconsistent", scale.getCode()));
			}
			mstScaleRepository.delete(scale);
		}
		}
	}
}
