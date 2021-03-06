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
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class TrnFXDayRepositoryTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TrnFXDayRepositoryTest.class);

	private static final double DELTA = 1e-15;

	@Autowired
	private TrnFXDayRepository fxDayRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxday/delete/input.xml")
	@ExpectedDatabase(value = "/data/fx/repositories/fxday/delete/expectedData.xml", table = "trn_fx_day")
	public void testDelete() throws Exception {
		// New FXDayKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
		// New Calendar data.
		Optional<TrnFXDay> fxDay = fxDayRepository.findOne(key);
		logger.debug("The FXDay is {}.", fxDay.get());
		// New Calendar data.
		fxDayRepository.delete(fxDay.get());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxday/find/input.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnFXDay> fxDays1 = fxDayRepository.findAll(new PageRequest(0, 10));
		assertEquals(15, fxDays1.getTotalElements());
		assertEquals(10, fxDays1.getNumberOfElements());
		assertEquals(2, fxDays1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnFXDay> fxDays2 = fxDayRepository.findAll(new PageRequest(1, 10));
		assertEquals(15, fxDays2.getTotalElements());
		assertEquals(5, fxDays2.getNumberOfElements());
		assertEquals(2, fxDays2.getTotalPages());

		// Retrieve all data from DB.
		List<TrnFXDay> fxDays = fxDayRepository.findAll();
		assertEquals(15, fxDays.size());

		// Count all data from DB.
		Long count = fxDayRepository.count();
		assertEquals(15, count.longValue());

		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
		// Get one from DB by key.
		Optional<TrnFXDay> fxDay = fxDayRepository.findOne(key);
		// Check result.
		assertEquals(true, fxDay.isPresent());
		key.setCurrencyPair("nzdjpy");
		// Get one from DB by key.
		fxDay = fxDayRepository.findOne(key);
		// Check result.
		assertEquals(false, fxDay.isPresent());

		// Test findPrevious method.
		Optional<TrnFXDay> fxPreviousDay = fxDayRepository.findPrevious("usdjpy", dateTime);
		// Check result.
		assertEquals(false, fxPreviousDay.isPresent());
		// Next day.
		fxPreviousDay = fxDayRepository.findPrevious("usdjpy", dateTime.plusDays(1));
		// Check result.
		assertEquals(true, fxPreviousDay.isPresent());
		assertEquals("usdjpy", fxPreviousDay.get().getTickKey().getCurrencyPair());
		assertEquals(2, fxPreviousDay.get().getOpeningPrice(), DELTA);
		assertEquals(4, fxPreviousDay.get().getHighPrice(), DELTA);
		assertEquals(1, fxPreviousDay.get().getLowPrice(), DELTA);
		assertEquals(3, fxPreviousDay.get().getFinishPrice(), DELTA);
		assertEquals(1.5, fxPreviousDay.get().getAvOpeningPrice(), DELTA);
		assertEquals(2.5, fxPreviousDay.get().getAvFinishPrice(), DELTA);
		// Another next day.
		fxPreviousDay = fxDayRepository.findPrevious("usdjpy", dateTime.plusDays(2));
		// Check result.
		assertEquals(true, fxPreviousDay.isPresent());
		assertEquals("usdjpy", fxPreviousDay.get().getTickKey().getCurrencyPair());
		assertEquals(20, fxPreviousDay.get().getOpeningPrice(), DELTA);
		assertEquals(40, fxPreviousDay.get().getHighPrice(), DELTA);
		assertEquals(10, fxPreviousDay.get().getLowPrice(), DELTA);
		assertEquals(30, fxPreviousDay.get().getFinishPrice(), DELTA);
		assertEquals(15, fxPreviousDay.get().getAvOpeningPrice(), DELTA);
		assertEquals(25, fxPreviousDay.get().getAvFinishPrice(), DELTA);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@ExpectedDatabase(value = "/data/fx/repositories/fxday/save/expectedData.xml", table = "trn_fx_day", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testSave() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
		// New TrnFXDay data.
		TrnFXDay fxDay = new TrnFXDay();
		fxDay.setTickKey(key);
		fxDay.setOpeningPrice(2000);
		fxDay.setHighPrice(4000);
		fxDay.setLowPrice(1000);
		fxDay.setFinishPrice(3000);
		fxDay.setAvOpeningPrice(1500);
		fxDay.setAvFinishPrice(2500);
		// Do save.
		fxDay = fxDayRepository.save(fxDay);
		// Check result.
		assertNotNull(fxDay.getCreatedBy());

		// Test update.
		fxDay.setOpeningPrice(200);
		fxDay.setHighPrice(400);
		fxDay.setLowPrice(100);
		fxDay.setFinishPrice(300);
		fxDay.setAvOpeningPrice(150);
		fxDay.setAvFinishPrice(250);
		// Do save.
		fxDay = fxDayRepository.save(fxDay);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxday/findinperiod/input.xml")
	public void testFindFXDayInPeriod() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
		// Get one from DB.
		List<TrnFXDay> ticks = fxDayRepository.findFXDayInPeriod("usdjpy", dateTime, dateTime.plusDays(1));
		// Check result.
		assertEquals(1, ticks.size());
		// Get one from DB.
		ticks = fxDayRepository.findFXDayInPeriod("usdjpy", dateTime, dateTime.plusWeeks(1));
		// Check result.
		assertEquals(7, ticks.size());
		// Get one from DB.
		ticks = fxDayRepository.findFXDayInPeriod("usdjpy", dateTime, dateTime.plusMonths(1));
		// Check result.
		assertEquals(11, ticks.size());
	}
}
