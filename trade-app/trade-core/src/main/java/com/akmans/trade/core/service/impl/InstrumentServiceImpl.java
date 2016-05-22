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
	private LocalContainerEntityManagerFactoryBean emf;

	@Autowired
	private MstInstrumentRepository mstInstrumentRepository;

	/**
	 * Find data for display on screen by specified query criteria.<br/>
	 * 
	 * @param InstrumentQueryDto dto<br/>
	 * @return Page<MstInstrument><br/>
	 */
	@SuppressWarnings("unchecked")
	public Page<MstInstrument> findPage(InstrumentQueryDto dto) {
		// Select statement.
		StringBuilder jpql = new StringBuilder();
		jpql.append("select instrument, scale, market, sector33, sector17 ");
		jpql.append("from MstInstrument as instrument ");
		jpql.append("left outer join instrument.market as market ");
		jpql.append("left outer join instrument.scale as scale ");
		jpql.append("left outer join instrument.sector17 as sector17 ");
		jpql.append("left outer join instrument.sector33 as sector33 ");
		// Count statement.
		StringBuilder cntJpql = new StringBuilder();
		cntJpql.append("select count(instrument.code) ");
		cntJpql.append("from MstInstrument as instrument ");
		cntJpql.append("left outer join instrument.market as market ");
		cntJpql.append("left outer join instrument.scale as scale ");
		cntJpql.append("left outer join instrument.sector17 as sector17 ");
		cntJpql.append("left outer join instrument.sector33 as sector33 ");
		// Query criteria.
		StringBuilder criteria = new StringBuilder();
		criteria.append("where 1 = 1 ");
		// Append instrument code.
		if (dto.getCode() != null) {
			criteria.append("and instrument.code = :code ");
		}
		// Append market code.
		if (dto.getMarket() != null) {
			criteria.append("and market.code = :market ");
		}
		// Append scale code.
		if (dto.getScale() != null) {
			criteria.append("and scale.code = :scale ");
		}
		// Append sector17 code.
		if (dto.getSector17() != null) {
			criteria.append("and sector17.code = :sector17 ");
		}
		// Append sector33 code.
		if (dto.getSector33() != null) {
			criteria.append("and sector33.code = :sector33 ");
		}
		// Append on board flag.
		if (dto.getOnboard() != null && dto.getOnboard().length() > 0) {
			criteria.append("and instrument.onboard = :onboard ");
		}
		// Set order by criteria.
		String orderByCriteria = "order by instrument.code asc ";
		// Create entity manager.
		EntityManager em = emf.getObject().createEntityManager();
		// Create select query.
		Query query = em.createQuery(cntJpql.toString() + criteria.toString());
		// Set parameter instrument code.
		if (dto.getCode() != null) {
			query.setParameter("code", dto.getCode());
		}
		// Set parameter market code.
		if (dto.getMarket() != null) {
			query.setParameter("market", dto.getMarket());
		}
		// Set parameter scale code.
		if (dto.getScale() != null) {
			query.setParameter("scale", dto.getScale());
		}
		// Set parameter sector17 code.
		if (dto.getSector17() != null) {
			query.setParameter("sector17", dto.getSector17());
		}
		// Set parameter sector33 code.
		if (dto.getSector33() != null) {
			query.setParameter("sector33", dto.getSector33());
		}
		// Set parameter on board flag.
		if (dto.getOnboard() != null && dto.getOnboard().length() > 0) {
			query.setParameter("onboard", dto.getOnboard());
		}
		// Get count.
		List<Object> cnt = query.getResultList();
		long count = (long) cnt.get(0);
		// Create select query.
		query = em.createQuery(jpql.toString() + criteria.toString() + orderByCriteria);
		// Set parameter instrument code.
		if (dto.getCode() != null) {
			query.setParameter("code", dto.getCode());
		}
		// Set parameter market code.
		if (dto.getMarket() != null) {
			query.setParameter("market", dto.getMarket());
		}
		// Set parameter scale code.
		if (dto.getScale() != null) {
			query.setParameter("scale", dto.getScale());
		}
		// Set parameter sector17 code.
		if (dto.getSector17() != null) {
			query.setParameter("sector17", dto.getSector17());
		}
		// Set parameter sector33 code.
		if (dto.getSector33() != null) {
			query.setParameter("sector33", dto.getSector33());
		}
		// Set parameter on board flag.
		if (dto.getOnboard() != null && dto.getOnboard().length() > 0) {
			query.setParameter("onboard", dto.getOnboard());
		}
		// Set first result.
		query.setFirstResult(dto.getPageable().getOffset());
		// Set max results (page size).
		query.setMaxResults(dto.getPageable().getPageSize());
		List<Object[]> list = query.getResultList();
		// Close entity manager.
		em.close();
		ArrayList<MstInstrument> content = new ArrayList<MstInstrument>();
		// Parsing select query result.
		for(Object[] item : list) {
			// First element to be instrument.
			MstInstrument instrument = (MstInstrument)item[0];
			// Second scale.
			instrument.setScale((MstScale)item[1]);
			// Third market.
			instrument.setMarket((MstMarket)item[2]);
			// Fourth sector33.
			instrument.setSector33((MstSector33)item[3]);
			// Fifth sector17.
			instrument.setSector17((MstSector17)item[4]);
			content.add(instrument);
		}
		// Return page content.
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
		MstInstrument instrument = null;
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
