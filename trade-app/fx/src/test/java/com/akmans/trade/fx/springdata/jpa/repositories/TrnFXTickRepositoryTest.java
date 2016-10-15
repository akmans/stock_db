package com.akmans.trade.fx.springdata.jpa.repositories;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class TrnFXTickRepositoryTest {

	private static final double DELTA = 1e-15;

	@Autowired
	private TrnFXTickRepository fxTickRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxTick/delete/input.xml")
	@ExpectedDatabase(value = "/data/fx/repositories/fxTick/delete/expectedData.xml", table = "trn_fx_tick")
	public void testDelete() throws Exception {
		// New FXTick data.
		Optional<TrnFXTick> fxTick = fxTickRepository.findOne(1001L);
		// New Calendar data.
		fxTickRepository.delete(fxTick.get());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxTick/find/input.xml")
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

		// Get one from DB by key.
		Optional<TrnFXTick> fxTick = fxTickRepository.findOne(1007L);
		// Check result.
		assertEquals(true, fxTick.isPresent());
		// Get one from DB by key.
		fxTick = fxTickRepository.findOne(1018L);
		// Check result.
		assertEquals(false, fxTick.isPresent());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@ExpectedDatabase(value = "/data/fx/repositories/fxTick/save/expectedData.xml", table = "trn_fx_tick", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testSave() throws Exception {
		// New DateTime
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		// New TrnFXTick data.
		TrnFXTick fxTick = new TrnFXTick();
		fxTick.setCurrencyPair("usdjpy");
		fxTick.setRegistDate(dateTime);
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
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxtick/findinperiod/input.xml")
	public void testCountFXTickInPeriod() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		// Get one from DB.
		int count = fxTickRepository.countFXTickInPeriod("usdjpy", dateTime, dateTime.plusHours(1));
		// Check result.
		assertEquals(4, count);
		// Get one from DB.
		count = fxTickRepository.countFXTickInPeriod("usdjpy", dateTime, dateTime.plusHours(6));
		// Check result.
		assertEquals(6, count);
		// Get one from DB.
		count = fxTickRepository.countFXTickInPeriod("usdjpy", dateTime, dateTime.plusDays(1));
		// Check result.
		assertEquals(8, count);
		// Get one from DB.
		count = fxTickRepository.countFXTickInPeriod("usdjpy", dateTime, dateTime.plusWeeks(1));
		// Check result.
		assertEquals(10, count);
		// Get one from DB.
		count = fxTickRepository.countFXTickInPeriod("usdjpy", dateTime, dateTime.plusMonths(1));
		// Check result.
		assertEquals(12, count);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxtick/findinperiod/input.xml")
	public void testFindFirstFXTickInPeriod() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		// Get one from DB.
		List<TrnFXTick> ticks = fxTickRepository.findFirstFXTickInPeriod("usdjpy", dateTime, dateTime.plusHours(1));
		// Check result.
		assertEquals(100, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findFirstFXTickInPeriod("usdjpy", dateTime, dateTime.plusHours(6));
		// Check result.
		assertEquals(100, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findFirstFXTickInPeriod("usdjpy", dateTime, dateTime.plusDays(1));
		// Check result.
		assertEquals(100, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findFirstFXTickInPeriod("usdjpy", dateTime, dateTime.plusWeeks(1));
		// Check result.
		assertEquals(100, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findFirstFXTickInPeriod("usdjpy", dateTime, dateTime.plusMonths(1));
		// Check result.
		assertEquals(100, ticks.get(0).getMidPrice(), DELTA);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxtick/findinperiod/input.xml")
	public void testFindLastFXTickInPeriod() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		// Get one from DB.
		List<TrnFXTick> ticks = fxTickRepository.findLastFXTickInPeriod("usdjpy", dateTime, dateTime.plusHours(1));
		// Check result.
		assertEquals(111, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findLastFXTickInPeriod("usdjpy", dateTime, dateTime.plusHours(6));
		// Check result.
		assertEquals(103, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findLastFXTickInPeriod("usdjpy", dateTime, dateTime.plusDays(1));
		// Check result.
		assertEquals(105, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findLastFXTickInPeriod("usdjpy", dateTime, dateTime.plusWeeks(1));
		// Check result.
		assertEquals(107, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findLastFXTickInPeriod("usdjpy", dateTime, dateTime.plusMonths(1));
		// Check result.
		assertEquals(109, ticks.get(0).getMidPrice(), DELTA);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxtick/findinperiod/input.xml")
	public void testFindHighestFXTickInPeriod() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		// Get one from DB.
		List<TrnFXTick> ticks = fxTickRepository.findHighestFXTickInPeriod("usdjpy", dateTime, dateTime.plusHours(1));
		// Check result.
		assertEquals(112, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findHighestFXTickInPeriod("usdjpy", dateTime, dateTime.plusHours(6));
		// Check result.
		assertEquals(112, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findHighestFXTickInPeriod("usdjpy", dateTime, dateTime.plusDays(1));
		// Check result.
		assertEquals(112, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findHighestFXTickInPeriod("usdjpy", dateTime, dateTime.plusWeeks(1));
		// Check result.
		assertEquals(112, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findHighestFXTickInPeriod("usdjpy", dateTime, dateTime.plusMonths(1));
		// Check result.
		assertEquals(112, ticks.get(0).getMidPrice(), DELTA);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxtick/findinperiod/input.xml")
	public void testFindLowestFXTickInPeriod() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		// Get one from DB.
		List<TrnFXTick> ticks = fxTickRepository.findLowestFXTickInPeriod("usdjpy", dateTime, dateTime.plusHours(1));
		// Check result.
		assertEquals(99, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findLowestFXTickInPeriod("usdjpy", dateTime, dateTime.plusHours(6));
		// Check result.
		assertEquals(99, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findLowestFXTickInPeriod("usdjpy", dateTime, dateTime.plusDays(1));
		// Check result.
		assertEquals(99, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findLowestFXTickInPeriod("usdjpy", dateTime, dateTime.plusWeeks(1));
		// Check result.
		assertEquals(99, ticks.get(0).getMidPrice(), DELTA);
		// Get one from DB.
		ticks = fxTickRepository.findLowestFXTickInPeriod("usdjpy", dateTime, dateTime.plusMonths(1));
		// Check result.
		assertEquals(98, ticks.get(0).getMidPrice(), DELTA);
	}
}
