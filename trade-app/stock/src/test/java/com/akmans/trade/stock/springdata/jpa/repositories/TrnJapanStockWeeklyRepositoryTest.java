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
import com.akmans.trade.stock.service.JapanStockWeeklyService;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockWeekly;
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
public class TrnJapanStockWeeklyRepositoryTest {

	@Autowired
	private JapanStockWeeklyService japanStockWeeklyService;

	@Autowired
	private TrnJapanStockWeeklyRepository japanStockWeeklyRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/japanstockweekly/delete/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/japanstockweekly/delete/expectedData.xml", table = "trn_japan_stock_weekly")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testDelete() throws Exception {
		JapanStockKey key = new JapanStockKey();
		key.setCode(1001);;
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 2, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// New JapanStockWeekly data.
		TrnJapanStockWeekly japanStockWeekly = japanStockWeeklyService.findOne(key);
		// New JapanStockWeekly data.
		japanStockWeeklyRepository.delete(japanStockWeekly);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/japanstockweekly/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnJapanStockWeekly> japanStockWeeklys1 = japanStockWeeklyRepository.findAll(new PageRequest(0, 10));
		assertEquals(18, japanStockWeeklys1.getTotalElements());
		assertEquals(10, japanStockWeeklys1.getNumberOfElements());
		assertEquals(2, japanStockWeeklys1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnJapanStockWeekly> japanStockWeeklys2 = japanStockWeeklyRepository.findAll(new PageRequest(1, 10));
		assertEquals(18, japanStockWeeklys2.getTotalElements());
		assertEquals(8, japanStockWeeklys2.getNumberOfElements());
		assertEquals(2, japanStockWeeklys2.getTotalPages());

		// Count all data from DB.
		Long count = japanStockWeeklyRepository.count();
		assertEquals(18, count.longValue());

		JapanStockKey key = new JapanStockKey();
		key.setCode(1000);
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 2, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// Get one from DB by key.
		Optional<TrnJapanStockWeekly> option = japanStockWeeklyRepository.findOne(key);
		// Check result.
		assertEquals(true, option.isPresent());
		temp.set(2015, 0, 10, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// Get one from DB by code = 13.
		option = japanStockWeeklyRepository.findOne(key);
		// Check result.
		assertEquals(false, option.isPresent());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/japanstockweekly/save/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/japanstockweekly/save/expectedData.xml", table = "trn_japan_stock_weekly", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testSave() throws Exception {
		JapanStockKey key = new JapanStockKey();
		key.setCode(1000);
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 1, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// New JapanStockWeekly data.
		TrnJapanStockWeekly japanStockWeekly = new TrnJapanStockWeekly();
		japanStockWeekly.setJapanStockKey(key);
		japanStockWeekly.setOpeningPrice(201);
		japanStockWeekly.setHighPrice(401);
		japanStockWeekly.setLowPrice(101);
		japanStockWeekly.setFinishPrice(301);
		japanStockWeekly.setTurnover(2001L);
		japanStockWeekly.setTradingValue(30001L);
		// Do save.
		japanStockWeekly = japanStockWeeklyRepository.save(japanStockWeekly);
		// Check result.
		assertNotNull(japanStockWeekly.getCreatedDate());
		assertNotNull(japanStockWeekly.getUpdatedDate());
		assertEquals("system", japanStockWeekly.getCreatedBy());

		// Test update.
		japanStockWeekly.setOpeningPrice(200);
		japanStockWeekly.setHighPrice(400);
		japanStockWeekly.setLowPrice(100);
		japanStockWeekly.setFinishPrice(300);
		japanStockWeekly.setTurnover(2000L);
		japanStockWeekly.setTradingValue(30000L);
		// Do save.
		japanStockWeekly = japanStockWeeklyRepository.save(japanStockWeekly);
		assertEquals("system", japanStockWeekly.getUpdatedBy());
	}
}
