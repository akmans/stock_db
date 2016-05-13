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

import com.akmans.trade.core.dto.CalendarQueryDto;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.CalendarService;
import com.akmans.trade.core.springdata.jpa.repositories.MstCalendarRepository;
import com.akmans.trade.core.springdata.jpa.entities.MstCalendar;
import com.akmans.trade.core.utils.CoreMessageUtils;

@Service
public class CalendarServiceImpl implements CalendarService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(CalendarServiceImpl.class);

	@Autowired
	private LocalContainerEntityManagerFactoryBean emf;

	@Autowired
	private MstCalendarRepository mstCalendarRepository;

	public Page<MstCalendar> findPage(CalendarQueryDto dto) {
		String jpql = "select cal from MstCalendar as cal ";
		String cntJpql = "select count(cal.code) from MstCalendar as cal ";
		String criteria = "where 1 = 1 ";
		if (dto.getCalendar() != null && dto.getCalendar().length() > 0) {
			criteria = criteria + "and cal.calendar like :calendar ";
		}
		if (dto.getHoliday() != null) {
			criteria = criteria + "and cal.holiday = :holiday ";
		}
		String orderByCriteria = "order by cal.registAt desc ";
		EntityManager em = emf.getObject().createEntityManager();
		Query query = em.createQuery(cntJpql + criteria);
		if (dto.getCalendar() != null && dto.getCalendar().length() > 0) {
			query.setParameter("calendar", dto.getCalendar());
		}
		if (dto.getHoliday() != null) {
			query.setParameter("holiday", dto.getHoliday());
		}
		List<Object> cnt = query.getResultList();
		long count = (long) cnt.get(0);
		query = em.createQuery(jpql + criteria + orderByCriteria);
		if (dto.getCalendar() != null && dto.getCalendar().length() > 0) {
			query.setParameter("calendar", dto.getCalendar());
		}
		if (dto.getHoliday() != null) {
			query.setParameter("holiday", dto.getHoliday());
		}
		query.setFirstResult(dto.getPageable().getOffset());
		query.setMaxResults(dto.getPageable().getPageSize());
		List<MstCalendar> list = query.getResultList();
		em.close();
		return count > 0 ? new PageImpl<MstCalendar>(list, dto.getPageable(), count)
				: new PageImpl<MstCalendar>(new ArrayList<MstCalendar>(), dto.getPageable(), 0);
	}

	public Page<MstCalendar> findAll(Pageable pageable) {
		Pageable pg = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "code");
		return mstCalendarRepository.findAll(pg);
	}

	public MstCalendar findOne(Long code) throws TradeException {
		Optional<MstCalendar> calendar = mstCalendarRepository.findOne(code);
		if (calendar.isPresent()) {
			return calendar.get();
		} else {
			throw new TradeException(CoreMessageUtils.getMessage("core.service.calendar.record.notfound", code));
		}
	}

	public void operation(MstCalendar calendar, OperationMode mode) throws TradeException {
		logger.debug("the calendar is {}", calendar);
		logger.debug("the mode is {}", mode);
		switch (mode) {
		case NEW: {
			List<MstCalendar> result = mstCalendarRepository.findByCalendarAndHoliday(calendar.getCalendar(),
					calendar.getHoliday());
			if (result != null && result.size() > 0) {
				throw new TradeException(
						CoreMessageUtils.getMessage("core.service.calendar.record.alreadyexist", calendar.getCode()));
			}
			mstCalendarRepository.save(calendar);
			break;
		}
		case EDIT: {
			MstCalendar origin = findOne(calendar.getCode());
			if (!origin.getUpdatedDate().equals(calendar.getUpdatedDate())) {
				throw new TradeException(
						CoreMessageUtils.getMessage("core.service.calendar.record.inconsistent", calendar.getCode()));
			}
			mstCalendarRepository.save(calendar);
			break;
		}
		case DELETE: {
			MstCalendar origin = findOne(calendar.getCode());
			if (!origin.getUpdatedDate().equals(calendar.getUpdatedDate())) {
				throw new TradeException(
						CoreMessageUtils.getMessage("core.service.calendar.record.inconsistent", calendar.getCode()));
			}
			mstCalendarRepository.delete(calendar);
		}
		}
	}
}
