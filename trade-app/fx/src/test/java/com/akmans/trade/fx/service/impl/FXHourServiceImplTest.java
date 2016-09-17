package com.akmans.trade.fx.service.impl;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.core.enums.FXType;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
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
public class FXHourServiceImplTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXHourServiceImplTest.class);

	private static final double DELTA = 1e-15;

	@Autowired
	private FXHourService fxHourService;

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxhour/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFind() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB by key.
		Optional<TrnFXHour> hour = fxHourService.findOne(key);
		// Check result.
		assertTrue(hour.isPresent());
		assertEquals("usdjpy", hour.get().getTickKey().getCurrencyPair());
		assertEquals(200, hour.get().getOpeningPrice(), DELTA);
		assertEquals(400, hour.get().getHighPrice(), DELTA);
		assertEquals(100, hour.get().getLowPrice(), DELTA);
		assertEquals(300, hour.get().getFinishPrice(), DELTA);
		assertEquals(150, hour.get().getAvOpeningPrice(), DELTA);
		assertEquals(250, hour.get().getAvFinishPrice(), DELTA);

		// Test findPrevious method.
		Optional<TrnFXHour> fxPreviousHour = fxHourService.findPrevious(key);
		// Check result.
		assertEquals(false, fxPreviousHour.isPresent());
		// Next hour.
		key.setRegistDate(key.getRegistDate().plusHours(1));
		// Do fine.
		fxPreviousHour = fxHourService.findPrevious(key);
		// Check result.
		assertEquals(true, fxPreviousHour.isPresent());
		assertEquals("usdjpy", fxPreviousHour.get().getTickKey().getCurrencyPair());
		assertEquals(200, fxPreviousHour.get().getOpeningPrice(), DELTA);
		assertEquals(400, fxPreviousHour.get().getHighPrice(), DELTA);
		assertEquals(100, fxPreviousHour.get().getLowPrice(), DELTA);
		assertEquals(300, fxPreviousHour.get().getFinishPrice(), DELTA);
		assertEquals(150, fxPreviousHour.get().getAvOpeningPrice(), DELTA);
		assertEquals(250, fxPreviousHour.get().getAvFinishPrice(), DELTA);
		// Another next hour.
		key.setRegistDate(key.getRegistDate().plusHours(1));
		// Do find.
		fxPreviousHour = fxHourService.findPrevious(key);
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
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxhour/generate/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testGenerateFXPeriodData() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB.
		AbstractFXEntity hour = fxHourService.generateFXPeriodData(FXType.HOUR, "usdjpy", result);
		// Check result.
		assertNull(hour);

		// Get one from DB.
		AbstractFXEntity sixHour = fxHourService.generateFXPeriodData(FXType.SIXHOUR, "usdjpy", result);
		// Check result.
		assertEquals(true, sixHour instanceof TrnFX6Hour);
		assertEquals(10, sixHour.getOpeningPrice(), DELTA);
		assertEquals(23, sixHour.getHighPrice(), DELTA);
		assertEquals(99, sixHour.getLowPrice(), DELTA);
		assertEquals(4, sixHour.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity day = fxHourService.generateFXPeriodData(FXType.DAY, "usdjpy", result);
		// Check result.
		assertEquals(true, day instanceof TrnFXDay);
		assertEquals(10, day.getOpeningPrice(), DELTA);
		assertEquals(25, day.getHighPrice(), DELTA);
		assertEquals(99, day.getLowPrice(), DELTA);
		assertEquals(6, day.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity week = fxHourService.generateFXPeriodData(FXType.WEEK, "usdjpy", result);
		// Check result.
		assertEquals(true, week instanceof TrnFXWeek);
		assertEquals(10, week.getOpeningPrice(), DELTA);
		assertEquals(27, week.getHighPrice(), DELTA);
		assertEquals(99, week.getLowPrice(), DELTA);
		assertEquals(8, week.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity month = fxHourService.generateFXPeriodData(FXType.MONTH, "usdjpy", result);
		// Check result.
		assertEquals(true, month instanceof TrnFXMonth);
		assertEquals(10, month.getOpeningPrice(), DELTA);
		assertEquals(29, month.getHighPrice(), DELTA);
		assertEquals(99, month.getLowPrice(), DELTA);
		assertEquals(10, month.getFinishPrice(), DELTA);
	}

	@Test
	@ExpectedDatabase(value = "/data/fx/service/fxhour/operation/expectedData4Insert.xml", table = "trn_fx_hour", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get TrnFXTick data from DB.
		TrnFXHour fxHour = new TrnFXHour();
		fxHour.setTickKey(key);
		fxHour.setOpeningPrice(10);
		fxHour.setHighPrice(20);
		fxHour.setLowPrice(100);
		fxHour.setFinishPrice(0);
		fxHour.setAvOpeningPrice(11);
		fxHour.setAvFinishPrice(12);
		// Do insert.
		fxHourService.operation(fxHour, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxhour/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxhour/operation/expectedData4Update.xml", table = "trn_fx_hour", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get TrnFXTick data from DB.
		TrnFXHour fxHour = fxHourService.findOne(key).get();
		fxHour.setTickKey(key);
		fxHour.setOpeningPrice(100);
		fxHour.setHighPrice(200);
		fxHour.setLowPrice(1000);
		fxHour.setFinishPrice(10);
		fxHour.setAvOpeningPrice(11);
		fxHour.setAvFinishPrice(12);
		// Do insert.
		fxHourService.operation(fxHour, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxhour/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxhour/operation/expectedData4Delete.xml", table = "trn_fx_hour")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New TrnFXTick data.
		Optional<TrnFXHour> fxHour = fxHourService.findOne(key);
		// Delete one from DB.
		fxHourService.operation(fxHour.get(), OperationMode.DELETE);
	}
}
