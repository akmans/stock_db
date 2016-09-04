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
public class TrnJapanStockMonthlyRepositoryTest {

	@Autowired
	private JapanStockMonthlyService japanStockMonthlyService;

	@Autowired
	private TrnJapanStockMonthlyRepository japanStockMonthlyRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/japanstockmonthly/delete/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/japanstockmonthly/delete/expectedData.xml", table = "trn_japan_stock_monthly")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testDelete() throws Exception {
		JapanStockKey key = new JapanStockKey();
		key.setCode(1001);;
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 2, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// New JapanStockMonthly data.
		TrnJapanStockMonthly japanStockMonthly = japanStockMonthlyService.findOne(key);
		// New JapanStockMonthly data.
		japanStockMonthlyRepository.delete(japanStockMonthly);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/japanstockmonthly/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnJapanStockMonthly> japanStockMonthlys1 = japanStockMonthlyRepository.findAll(new PageRequest(0, 10));
		assertEquals(18, japanStockMonthlys1.getTotalElements());
		assertEquals(10, japanStockMonthlys1.getNumberOfElements());
		assertEquals(2, japanStockMonthlys1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnJapanStockMonthly> japanStockMonthlys2 = japanStockMonthlyRepository.findAll(new PageRequest(1, 10));
		assertEquals(18, japanStockMonthlys2.getTotalElements());
		assertEquals(8, japanStockMonthlys2.getNumberOfElements());
		assertEquals(2, japanStockMonthlys2.getTotalPages());

		// Count all data from DB.
		Long count = japanStockMonthlyRepository.count();
		assertEquals(18, count.longValue());

		JapanStockKey key = new JapanStockKey();
		key.setCode(1000);
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 2, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// Get one from DB by key.
		Optional<TrnJapanStockMonthly> option = japanStockMonthlyRepository.findOne(key);
		// Check result.
		assertEquals(true, option.isPresent());
		temp.set(2015, 0, 10, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// Get one from DB by code = 13.
		option = japanStockMonthlyRepository.findOne(key);
		// Check result.
		assertEquals(false, option.isPresent());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/japanstockmonthly/save/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/japanstockmonthly/save/expectedData.xml", table = "trn_japan_stock_monthly", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testSave() throws Exception {
		JapanStockKey key = new JapanStockKey();
		key.setCode(1000);
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 1, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// New JapanStockMonthly data.
		TrnJapanStockMonthly japanStockMonthly = new TrnJapanStockMonthly();
		japanStockMonthly.setJapanStockKey(key);
		japanStockMonthly.setOpeningPrice(201);
		japanStockMonthly.setHighPrice(401);
		japanStockMonthly.setLowPrice(101);
		japanStockMonthly.setFinishPrice(301);
		japanStockMonthly.setTurnover(2001L);
		japanStockMonthly.setTradingValue(30001L);
		// Do save.
		japanStockMonthly = japanStockMonthlyRepository.save(japanStockMonthly);
		// Check result.
		assertNotNull(japanStockMonthly.getCreatedDate());
		assertNotNull(japanStockMonthly.getUpdatedDate());
		assertEquals("system", japanStockMonthly.getCreatedBy());

		// Test update.
		japanStockMonthly.setOpeningPrice(200);
		japanStockMonthly.setHighPrice(400);
		japanStockMonthly.setLowPrice(100);
		japanStockMonthly.setFinishPrice(300);
		japanStockMonthly.setTurnover(2000L);
		japanStockMonthly.setTradingValue(30000L);
		// Do save.
		japanStockMonthly = japanStockMonthlyRepository.save(japanStockMonthly);
		assertEquals("system", japanStockMonthly.getUpdatedBy());
	}
}
