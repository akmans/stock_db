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
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;
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
public class TrnFXHourRepositoryTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TrnFXHourRepositoryTest.class);

	private static final double DELTA = 1e-15;

	@Autowired
	private TrnFXHourRepository fxHourRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/repositories/fxhour/delete/input.xml")
	@ExpectedDatabase(value = "/data/fx/repositories/fxhour/delete/expectedData.xml", table = "trn_fx_hour")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testDelete() throws Exception {
		// New FXHourKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New Calendar data.
		Optional<TrnFXHour> fxHour = fxHourRepository.findOne(key);
		logger.debug("The FXHour is {}.", fxHour.get());
		// New Calendar data.
		fxHourRepository.delete(fxHour.get());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/repositories/fxhour/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnFXHour> fxHours1 = fxHourRepository.findAll(new PageRequest(0, 10));
		assertEquals(15, fxHours1.getTotalElements());
		assertEquals(10, fxHours1.getNumberOfElements());
		assertEquals(2, fxHours1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnFXHour> fxHours2 = fxHourRepository.findAll(new PageRequest(1, 10));
		assertEquals(15, fxHours2.getTotalElements());
		assertEquals(5, fxHours2.getNumberOfElements());
		assertEquals(2, fxHours2.getTotalPages());

		// Retrieve all data from DB.
		List<TrnFXHour> fxHours = fxHourRepository.findAll();
		assertEquals(15, fxHours.size());

		// Count all data from DB.
		Long count = fxHourRepository.count();
		assertEquals(15, count.longValue());

		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB by key.
		Optional<TrnFXHour> fxHour = fxHourRepository.findOne(key);
		// Check result.
		assertEquals(true, fxHour.isPresent());
		key.setCurrencyPair("nzdjpy");
		// Get one from DB by key.
		fxHour = fxHourRepository.findOne(key);
		// Check result.
		assertEquals(false, fxHour.isPresent());

		// Test findPrevious method.
		Optional<TrnFXHour> fxPreviousHour = fxHourRepository.findPrevious("usdjpy", result);
		// Check result.
		assertEquals(false, fxPreviousHour.isPresent());
		// Next hour.
		fxPreviousHour = fxHourRepository.findPrevious("usdjpy", result.plusHours(1));
		// Check result.
		assertEquals(true, fxPreviousHour.isPresent());
		assertEquals("usdjpy", fxPreviousHour.get().getTickKey().getCurrencyPair());
		assertEquals(2, fxPreviousHour.get().getOpeningPrice(), DELTA);
		assertEquals(4, fxPreviousHour.get().getHighPrice(), DELTA);
		assertEquals(1, fxPreviousHour.get().getLowPrice(), DELTA);
		assertEquals(3, fxPreviousHour.get().getFinishPrice(), DELTA);
		assertEquals(1.5, fxPreviousHour.get().getAvOpeningPrice(), DELTA);
		assertEquals(2.5, fxPreviousHour.get().getAvFinishPrice(), DELTA);
		// Another next hour.
		fxPreviousHour = fxHourRepository.findPrevious("usdjpy", result.plusHours(2));
		// Check result.
		assertEquals(true, fxPreviousHour.isPresent());
		assertEquals("usdjpy", fxPreviousHour.get().getTickKey().getCurrencyPair());
		assertEquals(20, fxPreviousHour.get().getOpeningPrice(), DELTA);
		assertEquals(40, fxPreviousHour.get().getHighPrice(), DELTA);
		assertEquals(10, fxPreviousHour.get().getLowPrice(), DELTA);
		assertEquals(30, fxPreviousHour.get().getFinishPrice(), DELTA);
		assertEquals(15, fxPreviousHour.get().getAvOpeningPrice(), DELTA);
		assertEquals(25, fxPreviousHour.get().getAvFinishPrice(), DELTA);
	}

	@Test
	@ExpectedDatabase(value = "/data/fx/repositories/fxhour/save/expectedData.xml", table = "trn_fx_hour", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testSave() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New TrnFXHour data.
		TrnFXHour fxHour = new TrnFXHour();
		fxHour.setTickKey(key);
		fxHour.setOpeningPrice(2000);
		fxHour.setHighPrice(4000);
		fxHour.setLowPrice(1000);
		fxHour.setFinishPrice(3000);
		fxHour.setAvOpeningPrice(1500);
		fxHour.setAvFinishPrice(2500);
		// Do save.
		fxHour = fxHourRepository.save(fxHour);
		// Check result.
		assertNotNull(fxHour.getCreatedBy());

		// Test update.
		fxHour.setOpeningPrice(200);
		fxHour.setHighPrice(400);
		fxHour.setLowPrice(100);
		fxHour.setFinishPrice(300);
		fxHour.setAvOpeningPrice(150);
		fxHour.setAvFinishPrice(250);
		// Do save.
		fxHour = fxHourRepository.save(fxHour);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/repositories/fxhour/findinperiod/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFindFXHourInPeriod() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB.
		List<TrnFXHour> ticks = fxHourRepository.findFXHourInPeriod("usdjpy", result, result.plusHours(1));
		// Check result.
		assertEquals(4, ticks.size());
		// Get one from DB.
		ticks = fxHourRepository.findFXHourInPeriod("usdjpy", result, result.plusHours(6));
		// Check result.
		assertEquals(6, ticks.size());
		// Get one from DB.
		ticks = fxHourRepository.findFXHourInPeriod("usdjpy", result, result.plusDays(1));
		// Check result.
		assertEquals(8, ticks.size());
		// Get one from DB.
		ticks = fxHourRepository.findFXHourInPeriod("usdjpy", result, result.plusWeeks(1));
		// Check result.
		assertEquals(10, ticks.size());
		// Get one from DB.
		ticks = fxHourRepository.findFXHourInPeriod("usdjpy", result, result.plusMonths(1));
		// Check result.
		assertEquals(12, ticks.size());
	}
}
