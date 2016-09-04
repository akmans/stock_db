package com.akmans.trade.stock.springdata.jpa.repositories;

import static org.junit.Assert.*;

import java.util.List;
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
import com.akmans.trade.stock.service.JapanStockService;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStock;
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
public class TrnJapanStockRepositoryTest {

	@Autowired
	private JapanStockService japanStockService;

	@Autowired
	private TrnJapanStockRepository japanStockRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/japanstock/delete/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/japanstock/delete/expectedData.xml", table = "trn_japan_stock")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testDelete() throws Exception {
		JapanStockKey key = new JapanStockKey();
		key.setCode(1001);
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 2, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// New JapanStock data.
		TrnJapanStock japanStock = japanStockService.findOne(key);
		// New JapanStock data.
		japanStockRepository.delete(japanStock);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/japanstock/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnJapanStock> japanStocks1 = japanStockRepository.findAll(new PageRequest(0, 10));
		assertEquals(18, japanStocks1.getTotalElements());
		assertEquals(10, japanStocks1.getNumberOfElements());
		assertEquals(2, japanStocks1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnJapanStock> japanStocks2 = japanStockRepository.findAll(new PageRequest(1, 10));
		assertEquals(18, japanStocks2.getTotalElements());
		assertEquals(8, japanStocks2.getNumberOfElements());
		assertEquals(2, japanStocks2.getTotalPages());

		java.util.Calendar temp1 = java.util.Calendar.getInstance();
		temp1.set(2015, 0, 1, 0, 0, 0);
		java.util.Calendar temp2 = java.util.Calendar.getInstance();
		temp2.set(2015, 0, 5, 0, 0, 0);
		// Retrieve all data from DB.
		List<TrnJapanStock> japanStocks = japanStockRepository.findJapanStockInPeriod(1000, temp1.getTime(), temp2.getTime());
		assertEquals(5, japanStocks.size());

		// Count all data from DB.
		Long count = japanStockRepository.count();
		assertEquals(18, count.longValue());

		JapanStockKey key = new JapanStockKey();
		key.setCode(1000);;
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 2, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// Get one from DB by key.
		Optional<TrnJapanStock> option = japanStockRepository.findOne(key);
		// Check result.
		assertEquals(true, option.isPresent());
		temp.set(2015, 0, 10, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// Get one from DB by code = 13.
		option = japanStockRepository.findOne(key);
		// Check result.
		assertEquals(false, option.isPresent());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/japanstock/save/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/japanstock/save/expectedData.xml", table = "trn_japan_stock", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testSave() throws Exception {
		JapanStockKey key = new JapanStockKey();
		key.setCode(1000);
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 1, 0, 0, 0);
		key.setRegistDate(temp.getTime());
		// New JapanStock data.
		TrnJapanStock japanStock = new TrnJapanStock();
		japanStock.setJapanStockKey(key);
		japanStock.setOpeningPrice(201);
		japanStock.setHighPrice(401);
		japanStock.setLowPrice(101);
		japanStock.setFinishPrice(301);
		japanStock.setTurnover(2001L);
		japanStock.setTradingValue(30001L);
		// Do save.
		japanStock = japanStockRepository.save(japanStock);
		// Check result.
		assertNotNull(japanStock.getCreatedDate());
		assertNotNull(japanStock.getUpdatedDate());
		assertEquals("system", japanStock.getCreatedBy());

		// Test update.
		japanStock.setOpeningPrice(200);
		japanStock.setHighPrice(400);
		japanStock.setLowPrice(100);
		japanStock.setFinishPrice(300);
		japanStock.setTurnover(2000L);
		japanStock.setTradingValue(30000L);
		// Do save.
		japanStock = japanStockRepository.save(japanStock);
		assertEquals("system", japanStock.getUpdatedBy());
	}
}
