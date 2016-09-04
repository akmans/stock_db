package com.akmans.trade.stock.service.impl;

import static org.junit.Assert.*;

import java.util.List;

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
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.stock.service.MarketService;
import com.akmans.trade.stock.springdata.jpa.entities.MstMarket;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class MarketServiceImplTest {

	@Autowired
	private MarketService marketService;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/market/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve all data from DB.
		List<MstMarket> markets = marketService.findAll();
		assertEquals(13, markets.size());

		// Retrieve page data from DB.
		Page<MstMarket> markets1 = marketService.findAll(new PageRequest(0, 10));
		assertEquals(13, markets1.getTotalElements());
		assertEquals(10, markets1.getNumberOfElements());
		assertEquals(2, markets1.getTotalPages());
		// Retrieve page data from DB.
		Page<MstMarket> markets2 = marketService.findAll(new PageRequest(1, 10));
		assertEquals(13, markets2.getTotalElements());
		assertEquals(3, markets2.getNumberOfElements());
		assertEquals(2, markets2.getTotalPages());

		// Get one from DB by code = 1L.
		MstMarket market = marketService.findOne(1);
		// Check result.
		assertEquals("Market 1", market.getName());
		assertEquals("user1", market.getCreatedBy());
		assertNotNull(market.getCreatedDate());
		assertEquals("user2", market.getUpdatedBy());
		assertNotNull(market.getUpdatedDate());

		// Retrieve data from DB by name.
		market = marketService.findByName("Market 1");
		assertEquals(1, market.getCode().intValue());
		market = marketService.findByName("Market 100");
		assertNull(market);
	}

	@Test(expected = TradeException.class)
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/market/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFindByNameExpectedException() throws Exception {
		marketService.findByName("Market 10");
	}

	@Test
	@ExpectedDatabase(value = "/data/stock/service/market/operation/expectedData4Insert.xml", table = "mst_market", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		// New Market data.
		MstMarket market = new MstMarket();
		market.setCode(2);
		market.setName("Market 2");
		// Do insert.
		marketService.operation(market, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/market/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/stock/service/market/operation/expectedData4Update.xml", table = "mst_market", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		// Get one from DB by code = 10.
		MstMarket market = marketService.findOne(10);
		// New Market data.
		market.setName("Market Ten");
		// Do insert.
		marketService.operation(market, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/market/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/stock/service/market/operation/expectedData4Delete.xml", table = "mst_market")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		// New Market data.
		MstMarket market = marketService.findOne(0);
		// Delete one from DB by code = 0.
		marketService.operation(market, OperationMode.DELETE);
	}
}
