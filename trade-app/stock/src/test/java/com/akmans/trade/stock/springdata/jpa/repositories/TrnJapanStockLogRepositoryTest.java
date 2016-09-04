package com.akmans.trade.stock.springdata.jpa.repositories;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.core.utils.DateUtil;
import com.akmans.trade.stock.service.JapanStockLogService;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockLog;
import com.akmans.trade.stock.springdata.jpa.keys.JapanStockLogKey;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class TrnJapanStockLogRepositoryTest {

	@Autowired
	private JapanStockLogService japanStockLogService;

	@Autowired
	private TrnJapanStockLogRepository japanStockLogRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/japanstocklog/delete/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/japanstocklog/delete/expectedData.xml", table = "trn_japan_stock_log")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testDelete() throws Exception {
		JapanStockLogKey key = new JapanStockLogKey();
		key.setJobId("JOB_ID_2");
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 2, 0, 0, 0);
		key.setProcessDate(temp.getTime());
		// New JapanStockLog data.
		TrnJapanStockLog japanStockLog = japanStockLogService.findOne(key);
		// New JapanStockLog data.
		japanStockLogRepository.delete(japanStockLog);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/japanstocklog/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnJapanStockLog> japanStockLogs1 = japanStockLogRepository.findAll(new PageRequest(0, 10));
		assertEquals(16, japanStockLogs1.getTotalElements());
		assertEquals(10, japanStockLogs1.getNumberOfElements());
		assertEquals(2, japanStockLogs1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnJapanStockLog> japanStockLogs2 = japanStockLogRepository.findAll(new PageRequest(1, 10));
		assertEquals(16, japanStockLogs2.getTotalElements());
		assertEquals(6, japanStockLogs2.getNumberOfElements());
		assertEquals(2, japanStockLogs2.getTotalPages());

		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 5, 0, 0, 0);
		// Retrieve all data from DB.
		TrnJapanStockLog japanStockLog = japanStockLogRepository.findMaxRegistDate("JOB_ID_1", temp.getTime());
		assertEquals("2015-01-04",
				DateUtil.formatDate(japanStockLog.getJapanStockLogKey().getProcessDate(), "yyyy-MM-dd"));

		// Count all data from DB.
		Long count = japanStockLogRepository.count();
		assertEquals(16, count.longValue());

		JapanStockLogKey key = new JapanStockLogKey();
		key.setJobId("JOB_ID_2");
		temp.set(2015, 0, 2, 0, 0, 0);
		key.setProcessDate(temp.getTime());
		// Get one from DB by code = 1L.
		Optional<TrnJapanStockLog> option = japanStockLogRepository.findOne(key);
		// Check result.
		assertEquals(true, option.isPresent());
		temp.set(2015, 0, 1, 0, 0, 0);
		key.setProcessDate(temp.getTime());
		// Get one from DB by code = 13.
		option = japanStockLogRepository.findOne(key);
		// Check result.
		assertEquals(false, option.isPresent());
	}

	@Test
	@ExpectedDatabase(value = "/data/stock/repositories/japanstocklog/save/expectedData.xml", table = "trn_japan_stock_log", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testSave() throws Exception {
		JapanStockLogKey key = new JapanStockLogKey();
		key.setJobId("JOB_ID_1");
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 1, 0, 0, 0);
		key.setProcessDate(temp.getTime());
		// New JapanStockLog data.
		TrnJapanStockLog japanStockLog = new TrnJapanStockLog();
		japanStockLog.setJapanStockLogKey(key);
		japanStockLog.setStatus("RUNNING");
		// Do save.
		japanStockLog = japanStockLogRepository.save(japanStockLog);
		// Check result.
		assertNotNull(japanStockLog.getCreatedDate());
		assertNotNull(japanStockLog.getUpdatedDate());
		assertEquals("system", japanStockLog.getCreatedBy());

		// Test update.
		japanStockLog.setStatus("COMPLETED");
		// Do save.
		japanStockLog = japanStockLogRepository.save(japanStockLog);
		assertEquals("system", japanStockLog.getUpdatedBy());
	}
}
