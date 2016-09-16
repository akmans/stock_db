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
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
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
public class TrnFXDayRepositoryTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TrnFXDayRepositoryTest.class);

	@Autowired
	private TrnFXDayRepository fxDayRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/repositories/fxday/delete/input.xml")
	@ExpectedDatabase(value = "/data/fx/repositories/fxday/delete/expectedData.xml", table = "trn_fx_day")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testDelete() throws Exception {
		// New FXDayKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New Calendar data.
		Optional<TrnFXDay> fxDay = fxDayRepository.findOne(key);
		logger.debug("The FXDay is {}.", fxDay.get());
		// New Calendar data.
		fxDayRepository.delete(fxDay.get());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/repositories/fxday/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
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
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB by key.
		Optional<TrnFXDay> fxDay = fxDayRepository.findOne(key);
		// Check result.
		assertEquals(true, fxDay.isPresent());
		key.setCurrencyPair("nzdjpy");
		// Get one from DB by key.
		fxDay = fxDayRepository.findOne(key);
		// Check result.
		assertEquals(false, fxDay.isPresent());
	}

	@Test
	@ExpectedDatabase(value = "/data/fx/repositories/fxday/save/expectedData.xml", table = "trn_fx_day", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testSave() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
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
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/repositories/fxday/findinperiod/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFindFXDayInPeriod() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB.
		List<TrnFXDay> ticks = fxDayRepository.findFXDayInPeriod("usdjpy", result, result.plusDays(1));
		// Check result.
		assertEquals(1, ticks.size());
		// Get one from DB.
		ticks = fxDayRepository.findFXDayInPeriod("usdjpy", result, result.plusWeeks(1));
		// Check result.
		assertEquals(7, ticks.size());
		// Get one from DB.
		ticks = fxDayRepository.findFXDayInPeriod("usdjpy", result, result.plusMonths(1));
		// Check result.
		assertEquals(11, ticks.size());
	}
}
