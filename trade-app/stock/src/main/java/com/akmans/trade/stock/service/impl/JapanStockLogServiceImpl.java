package com.akmans.trade.stock.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.stock.dto.JapanStockLogQueryDto;
import com.akmans.trade.stock.service.JapanStockLogService;
import com.akmans.trade.stock.springdata.jpa.repositories.TrnJapanStockLogRepository;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockLog;
import com.akmans.trade.stock.springdata.jpa.keys.JapanStockLogKey;

@Service
public class JapanStockLogServiceImpl implements JapanStockLogService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockLogServiceImpl.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private LocalContainerEntityManagerFactoryBean emf;

	@Autowired
	private TrnJapanStockLogRepository trnJapanStockLogRepository;

	@SuppressWarnings("unchecked")
	public Page<TrnJapanStockLog> findPage(JapanStockLogQueryDto dto) {
		String jpql = "select log from TrnJapanStockLog as log ";
		String cntJpql = "select count(log.japanStockLogKey.jobId) from TrnJapanStockLog as log ";
		String criteria = "where 1 = 1 ";
		if (dto.getJobId() != null && dto.getJobId().length() > 0) {
			criteria = criteria + "and log.japanStockLogKey.jobId = :jobId ";
		}
		if (dto.getProcessDate() != null) {
			criteria = criteria + "and log.japanStockLogKey.processDate = :processDate ";
		}
		String orderByCriteria = "order by log.japanStockLogKey.processDate desc ";
		EntityManager em = emf.getObject().createEntityManager();
		Query query = em.createQuery(cntJpql + criteria);
		if (dto.getJobId() != null && dto.getJobId().length() > 0) {
			query.setParameter("jobId", dto.getJobId());
		}
		if (dto.getProcessDate() != null) {
			query.setParameter("processDate", dto.getProcessDate());
		}
		List<Object> cnt = query.getResultList();
		long count = (long) cnt.get(0);
		query = em.createQuery(jpql + criteria + orderByCriteria);
		if (dto.getJobId() != null && dto.getJobId().length() > 0) {
			query.setParameter("jobId", dto.getJobId());
		}
		if (dto.getProcessDate() != null) {
			query.setParameter("processDate", dto.getProcessDate());
		}
		query.setFirstResult(dto.getPageable().getOffset());
		query.setMaxResults(dto.getPageable().getPageSize());
		List<TrnJapanStockLog> list = query.getResultList();
		em.close();
		return count > 0 ? new PageImpl<TrnJapanStockLog>(list, dto.getPageable(), count)
				: new PageImpl<TrnJapanStockLog>(new ArrayList<TrnJapanStockLog>(), dto.getPageable(), 0);
	}

	public Optional<TrnJapanStockLog> findOne(JapanStockLogKey key) {
		return trnJapanStockLogRepository.findOne(key);
	}

	public TrnJapanStockLog operation(TrnJapanStockLog log, OperationMode mode) throws TradeException {
		logger.debug("the calendar is {}", log);
		logger.debug("the mode is {}", mode);
		Optional<TrnJapanStockLog> origin = findOne(log.getJapanStockLogKey());
		switch (mode) {
		case NEW: {
			if (origin.isPresent()) {
				throw new TradeException(messageService.getMessage("core.service.record.alreadyexist",
						log.getJapanStockLogKey().toString()));
			}
			return trnJapanStockLogRepository.save(log);
		}
		case EDIT: {
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(log.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent",
						log.getJapanStockLogKey().toString()));
			}
			return trnJapanStockLogRepository.save(log);
		}
		case DELETE: {
			if (!origin.isPresent() || !origin.get().getUpdatedDate().equals(log.getUpdatedDate())) {
				throw new TradeException(messageService.getMessage("core.service.record.inconsistent",
						log.getJapanStockLogKey().toString()));
			}
			trnJapanStockLogRepository.delete(log);
		}
		}
		return null;
	}

	public TrnJapanStockLog findMaxRegistDate(String jobId, Date processDate) {
		return trnJapanStockLogRepository.findMaxRegistDate(jobId, processDate);
		// TODO null check required.
	}
}
