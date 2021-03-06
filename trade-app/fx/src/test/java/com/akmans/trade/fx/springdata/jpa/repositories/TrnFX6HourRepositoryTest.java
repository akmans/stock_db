package com.akmans.trade.fx.springdata.jpa.repositories;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class TrnFX6HourRepositoryTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TrnFX6HourRepositoryTest.class);

	private static final double DELTA = 1e-15;

	@Autowired
	private TrnFX6HourRepository fx6HourRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fx6hour/delete/input.xml")
	@ExpectedDatabase(value = "/data/fx/repositories/fx6hour/delete/expectedData.xml", table = "trn_fx_6hour")
	public void testDelete() throws Exception {
		// New FX6HourKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:00:00.000", formatter);
		key.setRegistDate(dateTime);
		// New Calendar data.
		Optional<TrnFX6Hour> fx6Hour = fx6HourRepository.findOne(key);
		logger.debug("The FX6Hour is {}.", fx6Hour.get());
		// New Calendar data.
		fx6HourRepository.delete(fx6Hour.get());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fx6hour/find/input.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnFX6Hour> fx6Hours1 = fx6HourRepository.findAll(new PageRequest(0, 10));
		assertEquals(15, fx6Hours1.getTotalElements());
		assertEquals(10, fx6Hours1.getNumberOfElements());
		assertEquals(2, fx6Hours1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnFX6Hour> fx6Hours2 = fx6HourRepository.findAll(new PageRequest(1, 10));
		assertEquals(15, fx6Hours2.getTotalElements());
		assertEquals(5, fx6Hours2.getNumberOfElements());
		assertEquals(2, fx6Hours2.getTotalPages());

		// Retrieve all data from DB.
		List<TrnFX6Hour> fx6Hours = fx6HourRepository.findAll();
		assertEquals(15, fx6Hours.size());

		// Count all data from DB.
		Long count = fx6HourRepository.count();
		assertEquals(15, count.longValue());

		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
		// Get one from DB by key.
		Optional<TrnFX6Hour> fx6Hour = fx6HourRepository.findOne(key);
		// Check result.
		assertEquals(true, fx6Hour.isPresent());
		assertEquals("usdjpy", fx6Hour.get().getTickKey().getCurrencyPair());
		assertEquals(2, fx6Hour.get().getOpeningPrice(), DELTA);
		assertEquals(4, fx6Hour.get().getHighPrice(), DELTA);
		assertEquals(1, fx6Hour.get().getLowPrice(), DELTA);
		assertEquals(3, fx6Hour.get().getFinishPrice(), DELTA);
		assertEquals(1.5, fx6Hour.get().getAvOpeningPrice(), DELTA);
		assertEquals(2.5, fx6Hour.get().getAvFinishPrice(), DELTA);
		// Another test.
		key.setCurrencyPair("nzdjpy");
		// Get one from DB by key.
		fx6Hour = fx6HourRepository.findOne(key);
		// Check result.
		assertEquals(false, fx6Hour.isPresent());

		// Test findPrevious method.
		Optional<TrnFX6Hour> fxPrevious6Hour = fx6HourRepository.findPrevious("usdjpy", dateTime);
		// Check result.
		assertEquals(false, fxPrevious6Hour.isPresent());
		// Next 6hour.
		fxPrevious6Hour = fx6HourRepository.findPrevious("usdjpy", dateTime.plusHours(6));
		// Check result.
		assertEquals(true, fxPrevious6Hour.isPresent());
		assertEquals("usdjpy", fxPrevious6Hour.get().getTickKey().getCurrencyPair());
		assertEquals(2, fxPrevious6Hour.get().getOpeningPrice(), DELTA);
		assertEquals(4, fxPrevious6Hour.get().getHighPrice(), DELTA);
		assertEquals(1, fxPrevious6Hour.get().getLowPrice(), DELTA);
		assertEquals(3, fxPrevious6Hour.get().getFinishPrice(), DELTA);
		assertEquals(1.5, fxPrevious6Hour.get().getAvOpeningPrice(), DELTA);
		assertEquals(2.5, fxPrevious6Hour.get().getAvFinishPrice(), DELTA);
		// Another next 6hour.
		fxPrevious6Hour = fx6HourRepository.findPrevious("usdjpy", dateTime.plusHours(12));
		// Check result.
		assertEquals(true, fxPrevious6Hour.isPresent());
		assertEquals("usdjpy", fxPrevious6Hour.get().getTickKey().getCurrencyPair());
		assertEquals(20, fxPrevious6Hour.get().getOpeningPrice(), DELTA);
		assertEquals(40, fxPrevious6Hour.get().getHighPrice(), DELTA);
		assertEquals(10, fxPrevious6Hour.get().getLowPrice(), DELTA);
		assertEquals(30, fxPrevious6Hour.get().getFinishPrice(), DELTA);
		assertEquals(15, fxPrevious6Hour.get().getAvOpeningPrice(), DELTA);
		assertEquals(25, fxPrevious6Hour.get().getAvFinishPrice(), DELTA);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@ExpectedDatabase(value = "/data/fx/repositories/fx6hour/save/expectedData.xml", table = "trn_fx_6hour", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testSave() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:00:00.000", formatter);
		key.setRegistDate(dateTime);
		// New TrnFX6Hour data.
		TrnFX6Hour fx6Hour = new TrnFX6Hour();
		fx6Hour.setTickKey(key);
		fx6Hour.setOpeningPrice(2000);
		fx6Hour.setHighPrice(4000);
		fx6Hour.setLowPrice(1000);
		fx6Hour.setFinishPrice(3000);
		fx6Hour.setAvOpeningPrice(1500);
		fx6Hour.setAvFinishPrice(2500);
		// Do save.
		fx6Hour = fx6HourRepository.save(fx6Hour);
		// Check result.
		assertNotNull(fx6Hour.getCreatedBy());

		// Test update.
		fx6Hour.setOpeningPrice(200);
		fx6Hour.setHighPrice(400);
		fx6Hour.setLowPrice(100);
		fx6Hour.setFinishPrice(300);
		fx6Hour.setAvOpeningPrice(150);
		fx6Hour.setAvFinishPrice(250);
		// Do save.
		fx6Hour = fx6HourRepository.save(fx6Hour);
	}
}
