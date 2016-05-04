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
import com.akmans.trade.core.service.InstrumentService;
import com.akmans.trade.core.springdata.jpa.dao.MstInstrumentDao;
import com.akmans.trade.core.springdata.jpa.entities.MstInstrument;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;
import com.akmans.trade.core.springdata.jpa.entities.MstScale;
import com.akmans.trade.core.springdata.jpa.entities.MstSector17;
import com.akmans.trade.core.springdata.jpa.entities.MstSector33;
import com.akmans.trade.core.utils.CoreMessageUtils;

@Service
public class InstrumentServiceImpl implements InstrumentService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ScaleServiceImpl.class);

	@Autowired
	private MstInstrumentDao mstInstrumentDao;

	public Page<MstInstrument> findPage(Pageable pageable) {
		long count = mstInstrumentDao.count();
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		List<Object[]> list = mstInstrumentDao.findPage(pg);
		ArrayList<MstInstrument> content = new ArrayList<MstInstrument>();
		for(Object[] item : list) {
			MstInstrument instrument = (MstInstrument)item[0];
			instrument.setScale((MstScale)item[1]);
			instrument.setMarket((MstMarket)item[2]);
			instrument.setSector33((MstSector33)item[3]);
			instrument.setSector17((MstSector17)item[4]);
			content.add(instrument);
		}
		return count > 0 ? new PageImpl<MstInstrument>(content, pageable, count)
				: new PageImpl<MstInstrument>(new ArrayList<MstInstrument>(), pageable, 0);
	}

	public Page<MstInstrument> findAll(Pageable pageable) {
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		return mstInstrumentDao.findAll(pg);
	}

	public MstInstrument findOne(Integer code) throws TradeException {
		Optional<MstInstrument> instrument = mstInstrumentDao.findOne(code);
		if (instrument.isPresent()) {
			return instrument.get();
		} else {
			throw new TradeException(CoreMessageUtils.getMessage("core.service.instrument.record.notfound", code));
		}
	}

	public void operation(MstInstrument instrument, OperationMode mode) throws TradeException {
		logger.debug("the scale is {}", instrument);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			if (mstInstrumentDao.findOne(instrument.getCode()).isPresent()) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.instrument.record.alreadyexist",
						instrument.getCode()));
			}
			mstInstrumentDao.save(instrument);
			break;
		}
		case EDIT: {
			MstInstrument origin = findOne(instrument.getCode());
			if (!origin.getUpdatedDate().equals(instrument.getUpdatedDate())) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.instrument.record.inconsistent",
						instrument.getCode()));
			}
			mstInstrumentDao.save(instrument);
			break;
		}
		case DELETE: {
			MstInstrument origin = findOne(instrument.getCode());
			if (!origin.getUpdatedDate().equals(instrument.getUpdatedDate())) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.instrument.record.inconsistent",
						instrument.getCode()));
			}
			mstInstrumentDao.delete(instrument);
		}
		}
	}
}
