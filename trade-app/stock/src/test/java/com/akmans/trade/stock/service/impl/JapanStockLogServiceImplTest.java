package com.akmans.trade.stock.service.impl;

import static org.junit.Assert.*;

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
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.utils.DateUtil;
import com.akmans.trade.stock.dto.JapanStockLogQueryDto;
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
public class JapanStockLogServiceImplTest {

	@Autowired
	private JapanStockLogService japanStockLogService;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/japanstocklog/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Create search criteria.
		JapanStockLogQueryDto criteria1 = new JapanStockLogQueryDto();
		criteria1.setPageable(new PageRequest(0, 10));
		// Retrieve page data from DB.
		Page<TrnJapanStockLog> japanStockLogs1 = japanStockLogService.findPage(criteria1);
		assertEquals(16, japanStockLogs1.getTotalElements());
		assertEquals(10, japanStockLogs1.getNumberOfElements());
		assertEquals(2, japanStockLogs1.getTotalPages());
		// Change criteria1.
		criteria1.setPageable(new PageRequest(1, 10));
		// Retrieve page data from DB.
		Page<TrnJapanStockLog> japanStockLogs2 = japanStockLogService.findPage(criteria1);
		assertEquals(16, japanStockLogs2.getTotalElements());
		assertEquals(6, japanStockLogs2.getNumberOfElements());
		assertEquals(2, japanStockLogs2.getTotalPages());
		// Change criteria1.
		criteria1.setJobId("JOB_ID_1");
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 1, 0, 0, 0);
		criteria1.setProcessDate(temp.getTime());
		criteria1.setPageable(new PageRequest(0, 10));
		// Retrieve page data from DB.
		Page<TrnJapanStockLog> japanStockLogs3 = japanStockLogService.findPage(criteria1);
		assertEquals(1, japanStockLogs3.getTotalElements());
		assertEquals(1, japanStockLogs3.getNumberOfElements());
		assertEquals(1, japanStockLogs3.getTotalPages());

		JapanStockLogKey key = new JapanStockLogKey();
		key.setJobId("JOB_ID_1");
		temp.set(2015, 0, 1, 0, 0, 0);
		key.setProcessDate(temp.getTime());
		// Get one from DB by key.
		TrnJapanStockLog japanStockLog = japanStockLogService.findOne(key).get();
		// Check result.
		assertEquals("COMPLETED", japanStockLog.getStatus());
		assertEquals("user1", japanStockLog.getCreatedBy());
		assertNotNull(japanStockLog.getCreatedDate());
		assertEquals("user2", japanStockLog.getUpdatedBy());
		assertNotNull(japanStockLog.getUpdatedDate());

		temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 5, 0, 0, 0);
		// Retrieve data of max regist date.
		japanStockLog = japanStockLogService.findMaxRegistDate("JOB_ID_1", temp.getTime());
		assertEquals("2015-01-04", DateUtil.formatDate(japanStockLog.getJapanStockLogKey().getProcessDate(), "yyyy-MM-dd"));
	}

	@Test
	@ExpectedDatabase(value = "/data/stock/service/japanstocklog/operation/expectedData4Insert.xml", table = "trn_japan_stock_log", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		JapanStockLogKey key = new JapanStockLogKey();
		key.setJobId("JOB_ID_1");
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 1, 0, 0, 0);
		key.setProcessDate(temp.getTime());
		// New TrnJapanStock data.
		TrnJapanStockLog japanStockLog = new TrnJapanStockLog();
		japanStockLog.setJapanStockLogKey(key);
		japanStockLog.setStatus("COMPLETED");
		// Do insert.
		japanStockLogService.operation(japanStockLog, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/japanstocklog/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/stock/service/japanstocklog/operation/expectedData4Update.xml", table = "trn_japan_stock_log", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		JapanStockLogKey key = new JapanStockLogKey();
		key.setJobId("JOB_ID_1");
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 1, 0, 0, 0);
		key.setProcessDate(temp.getTime());
		// Get one from DB by code = 10.
		TrnJapanStockLog japanStockLog = japanStockLogService.findOne(key).get();
		// Update data.
		japanStockLog.setStatus("COMPLETED");
		// Do update.
		japanStockLogService.operation(japanStockLog, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/japanStockLog/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/stock/service/japanStockLog/operation/expectedData4Delete.xml", table = "trn_japan_stock_log")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		JapanStockLogKey key = new JapanStockLogKey();
		key.setJobId("JOB_ID_2");
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 2, 0, 0, 0);
		key.setProcessDate(temp.getTime());
		// New TrnJapanStock data.
		TrnJapanStockLog japanStockLog = japanStockLogService.findOne(key).get();
		// Delete one from DB by key.
		japanStockLogService.operation(japanStockLog, OperationMode.DELETE);
	}
}
