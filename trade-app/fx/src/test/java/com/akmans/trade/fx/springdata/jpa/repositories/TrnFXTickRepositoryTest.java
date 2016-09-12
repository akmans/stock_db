package com.akmans.trade.fx.springdata.jpa.repositories;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class TrnFXTickRepositoryTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TrnFXTickRepositoryTest.class);

	@Autowired
	private TrnFXTickRepository fxTickRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxTick/delete/input.xml")
	@ExpectedDatabase(value = "/data/fx/repositories/fxTick/delete/expectedData.xml", table = "trn_fx_tick")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testDelete() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New Calendar data.
		Optional<TrnFXTick> fxTick = fxTickRepository.findOne(key);
		// New Calendar data.
		fxTickRepository.delete(fxTick.get());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxTick/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnFXTick> fxTicks1 = fxTickRepository.findAll(new PageRequest(0, 10));
		assertEquals(18, fxTicks1.getTotalElements());
		assertEquals(10, fxTicks1.getNumberOfElements());
		assertEquals(2, fxTicks1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnFXTick> fxTicks2 = fxTickRepository.findAll(new PageRequest(1, 10));
		assertEquals(18, fxTicks2.getTotalElements());
		assertEquals(8, fxTicks2.getNumberOfElements());
		assertEquals(2, fxTicks2.getTotalPages());

		// Retrieve all data from DB.
		List<TrnFXTick> fxTicks = fxTickRepository.findAll();
		assertEquals(18, fxTicks.size());

		// Count all data from DB.
		Long count = fxTickRepository.count();
		assertEquals(18, count.longValue());

		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB by key.
		Optional<TrnFXTick> fxTick = fxTickRepository.findOne(key);
		// Check result.
		assertEquals(true, fxTick.isPresent());
		key.setCurrencyPair("nzdjpy");
		// Get one from DB by key.
		fxTick = fxTickRepository.findOne(key);
		// Check result.
		assertEquals(false, fxTick.isPresent());
	}

	@Test
	@ExpectedDatabase(value = "/data/fx/repositories/fxTick/save/expectedData.xml", table = "trn_fx_tick", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testSave() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New TrnFXTick data.
		TrnFXTick fxTick = new TrnFXTick();
		fxTick.setTickKey(key);
		fxTick.setAskPrice(1000);
		fxTick.setBidPrice(2000);
		fxTick.setMidPrice(1500);
		fxTick.setProcessedFlag(1);
		// Do save.
		fxTick = fxTickRepository.save(fxTick);
		// Check result.
		assertNotNull(fxTick.getCreatedBy());

		// Test update.
		fxTick.setAskPrice(200);
		fxTick.setBidPrice(100);
		fxTick.setMidPrice(150);
		fxTick.setProcessedFlag(0);
		// Do save.
		fxTick = fxTickRepository.save(fxTick);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxtick/findinperiod/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFindFXTickInPeriod() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB.
		List<TrnFXTick> ticks = fxTickRepository.findFXTickInPeriod("usdjpy", result, result.plusHours(1));
		// Check result.
		assertEquals(4, ticks.size());
		// Get one from DB.
		ticks = fxTickRepository.findFXTickInPeriod("usdjpy", result, result.plusHours(6));
		// Check result.
		assertEquals(6, ticks.size());
		// Get one from DB.
		ticks = fxTickRepository.findFXTickInPeriod("usdjpy", result, result.plusDays(1));
		// Check result.
		assertEquals(8, ticks.size());
		// Get one from DB.
		ticks = fxTickRepository.findFXTickInPeriod("usdjpy", result, result.plusWeeks(1));
		// Check result.
		assertEquals(10, ticks.size());
		// Get one from DB.
		ticks = fxTickRepository.findFXTickInPeriod("usdjpy", result, result.plusMonths(1));
		// Check result.
		assertEquals(12, ticks.size());
	}
}
