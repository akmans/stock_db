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
public class MstMarketRepositoryTest {

	@Autowired
	private MarketService marketService;

	@Autowired
	private MstMarketRepository marketRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/market/delete/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/market/delete/expectedData.xml", table = "mst_market")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testDelete() throws Exception {
		// New Market data.
		MstMarket market = marketService.findOne(0);
		// New Market data.
		marketRepository.delete(market);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/market/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<MstMarket> markets1 = marketRepository.findAll(new PageRequest(0, 10));
		assertEquals(13, markets1.getTotalElements());
		assertEquals(10, markets1.getNumberOfElements());
		assertEquals(2, markets1.getTotalPages());
		// Retrieve second page data from DB.
		Page<MstMarket> markets2 = marketRepository.findAll(new PageRequest(1, 10));
		assertEquals(13, markets2.getTotalElements());
		assertEquals(3, markets2.getNumberOfElements());
		assertEquals(2, markets2.getTotalPages());

		// Retrieve all data from DB.
		List<MstMarket> markets = marketRepository.findAll();
		assertEquals(13, markets.size());

		// Retrieve all data from DB with Order by code ascending.
		markets = marketRepository.findAllByOrderByCodeAsc();
		assertEquals(0, markets.get(0).getCode().intValue());
		assertEquals(1, markets.get(1).getCode().intValue());
		assertEquals(12, markets.get(12).getCode().intValue());

		// Retrieve data from DB by name.
		markets = marketRepository.findByName("Market 10");
		assertEquals(3, markets.size());

		// Count all data from DB.
		Long count = marketRepository.count();
		assertEquals(13, count.longValue());

		// Get one from DB by code = 1L.
		Optional<MstMarket> market = marketRepository.findOne(1);
		// Check result.
		assertEquals(true, market.isPresent());
		// Get one from DB by code = 13.
		market = marketRepository.findOne(13);
		// Check result.
		assertEquals(false, market.isPresent());
	}

	@Test
	@ExpectedDatabase(value = "/data/stock/repositories/market/save/expectedData.xml", table = "mst_market", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testSave() throws Exception {
		// New Market data.
		MstMarket market = new MstMarket();
		market.setCode(21);
		market.setName("Market 2");
		// Do save.
		market = marketRepository.save(market);
		// Check result.
		assertNotNull(market.getCreatedDate());
		assertNotNull(market.getUpdatedDate());

		// Test update.
		market.setCode(21);
		market.setName("Market 21");
		// Do save.
		market = marketRepository.save(market);
	}
}
