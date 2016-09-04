package com.akmans.trade.stock.service.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.stock.service.JapanStockMonthlyService;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockMonthly;
import com.akmans.trade.stock.springdata.jpa.keys.JapanStockKey;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class JapanStockMonthlyServiceImplTest {

	@Autowired
	private JapanStockMonthlyService japanStockMonthlyService;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/japanstockmonthly/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		JapanStockKey key = new JapanStockKey();
		key.setCode(1001);;
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 2, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// Retrieve one data from DB.
		TrnJapanStockMonthly japanStock = japanStockMonthlyService.findOne(key);
		assertEquals(201, japanStock.getOpeningPrice().intValue());
		assertEquals(401, japanStock.getHighPrice().intValue());
		assertEquals(101, japanStock.getLowPrice().intValue());
		assertEquals(301, japanStock.getFinishPrice().intValue());
		assertEquals(2001L, japanStock.getTurnover().longValue());
		assertEquals(30001L, japanStock.getTradingValue().longValue());

		// Check exist.
		JapanStockKey key1 = new JapanStockKey();
		key1.setCode(1000);
		temp.set(2015, 0, 9, 0, 0, 0);
		key1.setRegistDate(temp.getTime());
		// Get one from DB by key.
		boolean result1 = japanStockMonthlyService.exist(key1);
		// Check result.
		assertEquals(true, result1);
		// Check exist.
		JapanStockKey key2 = new JapanStockKey();
		key2.setCode(1000);
		temp.set(2015, 0, 10, 0, 0, 0);
		key2.setRegistDate(temp.getTime());
		// Get one from DB by key.
		boolean result2 = japanStockMonthlyService.exist(key2);
		// Check result.
		assertEquals(false, result2);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/japanstockmonthly/operation/input4Insert.xml")
	@ExpectedDatabase(value = "/data/stock/service/japanstockmonthly/operation/expectedData4Insert.xml", table = "trn_japan_stock_monthly", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		JapanStockKey key = new JapanStockKey();
		key.setCode(1000);
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 1, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// New TrnJapanStock data.
		TrnJapanStockMonthly japanStock = new TrnJapanStockMonthly();
		japanStock.setJapanStockKey(key);
		japanStock.setOpeningPrice(200);
		japanStock.setHighPrice(400);
		japanStock.setLowPrice(100);
		japanStock.setFinishPrice(300);
		japanStock.setTurnover(2000L);
		japanStock.setTradingValue(30000L);
		// Do insert.
		japanStockMonthlyService.operation(japanStock, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/japanstockmonthly/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/stock/service/japanstockmonthly/operation/expectedData4Update.xml", table = "trn_japan_stock_monthly", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		JapanStockKey key = new JapanStockKey();
		key.setCode(1000);
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 1, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// Get one from DB by key.
		TrnJapanStockMonthly japanStock = japanStockMonthlyService.findOne(key);
		// Update data.
		japanStock.setOpeningPrice(201);
		japanStock.setHighPrice(401);
		japanStock.setLowPrice(101);
		japanStock.setFinishPrice(301);
		japanStock.setTurnover(2001L);
		japanStock.setTradingValue(30001L);
		// Do update.
		japanStockMonthlyService.operation(japanStock, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/japanstockmonthly/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/stock/service/japanstockmonthly/operation/expectedData4Delete.xml", table = "trn_japan_stock_monthly")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		JapanStockKey key = new JapanStockKey();
		key.setCode(1000);
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 2, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// New TrnJapanStock data.
		TrnJapanStockMonthly japanStock = japanStockMonthlyService.findOne(key);
		// Delete one from DB by key.
		japanStockMonthlyService.operation(japanStock, OperationMode.DELETE);
	}
}
