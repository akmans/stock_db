package com.akmans.trade.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.SpecialDetailService;
import com.akmans.trade.core.springdata.jpa.dao.TrnSpecialDetailRepository;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialDetail;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.core.utils.CoreMessageUtils;

@Service
public class SpecialDetailServiceImpl implements SpecialDetailService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ScaleServiceImpl.class);

	@Autowired
	private TrnSpecialDetailRepository trnSpecialDetailRepository;

	public Page<TrnSpecialDetail> findPage(Pageable pageable) {
		long count = trnSpecialDetailRepository.count();
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		List<Object[]> list = trnSpecialDetailRepository.findPage(pg);
		ArrayList<TrnSpecialDetail> content = new ArrayList<TrnSpecialDetail>();
		for(Object[] item : list) {
			TrnSpecialDetail specialDetail = (TrnSpecialDetail)item[0];
			specialDetail.setSpecialItem((TrnSpecialItem)item[1]);
			content.add(specialDetail);
		}
		return count > 0 ? new PageImpl<TrnSpecialDetail>(content, pageable, count)
				: new PageImpl<TrnSpecialDetail>(new ArrayList<TrnSpecialDetail>(), pageable, 0);
	}

	public Page<TrnSpecialDetail> findAll(Pageable pageable) {
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		return trnSpecialDetailRepository.findAll(pg);
	}

	public TrnSpecialDetail findOne(Long code) throws TradeException {
		Optional<TrnSpecialDetail> specialDetail = trnSpecialDetailRepository.findOne(code);
		if (specialDetail.isPresent()) {
			return specialDetail.get();
		} else {
			throw new TradeException(CoreMessageUtils.getMessage("core.service.specialDetail.record.notfound", code));
		}
	}

	public void operation(TrnSpecialDetail specialDetail, OperationMode mode) throws TradeException {
		logger.debug("the scale is {}", specialDetail);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
/*			if (trnSpecialDetailRepository.findOne(specialDetail.getCode()).isPresent()) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.specialDetail.record.alreadyexist",
						specialDetail.getCode()));
			}*/
			trnSpecialDetailRepository.save(specialDetail);
			break;
		}
		case EDIT: {
			TrnSpecialDetail origin = findOne(specialDetail.getCode());
			if (!origin.getUpdatedDate().equals(specialDetail.getUpdatedDate())) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.specialDetail.record.inconsistent",
						specialDetail.getCode()));
			}
			trnSpecialDetailRepository.save(specialDetail);
			break;
		}
		case DELETE: {
			TrnSpecialDetail origin = findOne(specialDetail.getCode());
			if (!origin.getUpdatedDate().equals(specialDetail.getUpdatedDate())) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.specialDetail.record.inconsistent",
						specialDetail.getCode()));
			}
			trnSpecialDetailRepository.delete(specialDetail);
		}
		}
	}

	public TrnSpecialDetail findOneEager(Long code) throws TradeException {
		List<Object[]> list = trnSpecialDetailRepository.findOneEager(code);
		TrnSpecialDetail specialDetail = new TrnSpecialDetail();
		for(Object[] item : list) {
			specialDetail = (TrnSpecialDetail)item[0];
			specialDetail.setSpecialItem((TrnSpecialItem)item[1]);
		}
		return specialDetail;
	}
}
