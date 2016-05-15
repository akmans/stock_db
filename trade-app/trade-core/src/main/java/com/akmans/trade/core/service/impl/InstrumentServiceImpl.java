package com.akmans.trade.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.dto.InstrumentQueryDto;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.InstrumentService;
import com.akmans.trade.core.springdata.jpa.repositories.MstInstrumentRepository;
import com.akmans.trade.core.springdata.jpa.entities.MstCalendar;
import com.akmans.trade.core.springdata.jpa.entities.MstInstrument;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;
import com.akmans.trade.core.springdata.jpa.entities.MstScale;
import com.akmans.trade.core.springdata.jpa.entities.MstSector17;
import com.akmans.trade.core.springdata.jpa.entities.MstSector33;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialDetail;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.core.utils.CoreMessageUtils;

@Service
public class InstrumentServiceImpl implements InstrumentService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ScaleServiceImpl.class);

	@Autowired
	private LocalContainerEntityManagerFactoryBean emf;

	@Autowired
	private MstInstrumentRepository mstInstrumentRepository;

	public Page<MstInstrument> findPage(InstrumentQueryDto dto) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("select instrument, scale, market, sector33, sector17 ");
		jpql.append("from MstInstrument as instrument ");
		jpql.append("left outer join instrument.market as market ");
		jpql.append("left outer join instrument.scale as scale ");
		jpql.append("left outer join instrument.sector17 as sector17 ");
		jpql.append("left outer join instrument.sector33 as sector33 ");
		StringBuilder cntJpql = new StringBuilder();
		cntJpql.append("select count(instrument.code) ");
		cntJpql.append("from MstInstrument as instrument ");
		cntJpql.append("left outer join instrument.market as market ");
		cntJpql.append("left outer join instrument.scale as scale ");
		cntJpql.append("left outer join instrument.sector17 as sector17 ");
		cntJpql.append("left outer join instrument.sector33 as sector33 ");
//		String jpql = "select instrument from MstInstrument as instrument ";
//		String cntJpql = "select count(cal.code) from MstCalendar as cal ";
		StringBuilder criteria = new StringBuilder();
		criteria.append("where 1 = 1 ");
		if (dto.getCode() != null) {
			criteria.append("and instrument.code = :code ");
		}
		if (dto.getMarket() != null) {
			criteria.append("and market.code = :market ");
		}
		if (dto.getScale() != null) {
			criteria.append("and scale.code = :scale ");
		}
		if (dto.getSector17() != null) {
			criteria.append("and sector17.code = :sector17 ");
		}
		if (dto.getSector33() != null) {
			criteria.append("and sector33.code = :sector33 ");
		}
		if (dto.getOnboard() != null && dto.getOnboard().length() > 0) {
			criteria.append("and instrument.onboard = :onboard ");
		}
		String orderByCriteria = "order by instrument.code asc ";
		EntityManager em = emf.getObject().createEntityManager();
		Query query = em.createQuery(cntJpql.toString() + criteria.toString());
		if (dto.getCode() != null) {
			query.setParameter("code", dto.getCode());
		}
		if (dto.getMarket() != null) {
			query.setParameter("market", dto.getMarket());
		}
		if (dto.getScale() != null) {
			query.setParameter("scale", dto.getScale());
		}
		if (dto.getSector17() != null) {
			query.setParameter("sector17", dto.getSector17());
		}
		if (dto.getSector33() != null) {
			query.setParameter("sector33", dto.getSector33());
		}
		if (dto.getOnboard() != null && dto.getOnboard().length() > 0) {
			query.setParameter("onboard", dto.getOnboard());
		}
		List<Object> cnt = query.getResultList();
		long count = (long) cnt.get(0);
		query = em.createQuery(jpql.toString() + criteria.toString() + orderByCriteria);
		if (dto.getCode() != null) {
			query.setParameter("code", dto.getCode());
		}
		if (dto.getMarket() != null) {
			query.setParameter("market", dto.getMarket());
		}
		if (dto.getScale() != null) {
			query.setParameter("scale", dto.getScale());
		}
		if (dto.getSector17() != null) {
			query.setParameter("sector17", dto.getSector17());
		}
		if (dto.getSector33() != null) {
			query.setParameter("sector33", dto.getSector33());
		}
		if (dto.getOnboard() != null && dto.getOnboard().length() > 0) {
			query.setParameter("onboard", dto.getOnboard());
		}
		query.setFirstResult(dto.getPageable().getOffset());
		query.setMaxResults(dto.getPageable().getPageSize());
		List<Object[]> list = query.getResultList();
		em.close();
/*		long count = mstInstrumentRepository.count();
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		List<Object[]> list = mstInstrumentRepository.findPage(pg);*/
		ArrayList<MstInstrument> content = new ArrayList<MstInstrument>();
		for(Object[] item : list) {
			MstInstrument instrument = (MstInstrument)item[0];
			instrument.setScale((MstScale)item[1]);
			instrument.setMarket((MstMarket)item[2]);
			instrument.setSector33((MstSector33)item[3]);
			instrument.setSector17((MstSector17)item[4]);
			content.add(instrument);
		}
		return count > 0 ? new PageImpl<MstInstrument>(content, dto.getPageable(), count)
				: new PageImpl<MstInstrument>(new ArrayList<MstInstrument>(), dto.getPageable(), 0);
	}

	public Page<MstInstrument> findAll(Pageable pageable) {
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		return mstInstrumentRepository.findAll(pg);
	}

	public MstInstrument findOne(Long code) throws TradeException {
		Optional<MstInstrument> instrument = mstInstrumentRepository.findOne(code);
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
			if (mstInstrumentRepository.findOne(instrument.getCode()).isPresent()) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.instrument.record.alreadyexist",
						instrument.getCode()));
			}
			mstInstrumentRepository.save(instrument);
			break;
		}
		case EDIT: {
			MstInstrument origin = findOne(instrument.getCode());
			if (!origin.getUpdatedDate().equals(instrument.getUpdatedDate())) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.instrument.record.inconsistent",
						instrument.getCode()));
			}
			mstInstrumentRepository.save(instrument);
			break;
		}
		case DELETE: {
			MstInstrument origin = findOne(instrument.getCode());
			if (!origin.getUpdatedDate().equals(instrument.getUpdatedDate())) {
				throw new TradeException(CoreMessageUtils.getMessage("core.service.instrument.record.inconsistent",
						instrument.getCode()));
			}
			mstInstrumentRepository.delete(instrument);
		}
		}
	}

	public MstInstrument findOneEager(Long code) throws TradeException {
		List<Object[]> list = mstInstrumentRepository.findOneEager(code);
		MstInstrument instrument = new MstInstrument();
		for (Object[] item : list) {
			instrument = (MstInstrument) item[0];
			instrument.setScale((MstScale) item[1]);
			instrument.setMarket((MstMarket) item[2]);
			instrument.setSector33((MstSector33) item[3]);
			instrument.setSector17((MstSector17) item[4]);
		}
		return instrument;
	}
}
