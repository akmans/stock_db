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
import com.akmans.trade.fx.service.FXDayService;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
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
public class FXDayServiceImplTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXDayServiceImplTest.class);

	private static final double DELTA = 1e-15;

	@Autowired
	private FXDayService fxDayService;

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxday/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFind() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB by key.
		Optional<TrnFXDay> day = fxDayService.findOne(key);
		// Check result.
		assertTrue(day.isPresent());
		assertEquals("usdjpy", day.get().getTickKey().getCurrencyPair());
		assertEquals(200, day.get().getOpeningPrice(), DELTA);
		assertEquals(400, day.get().getHighPrice(), DELTA);
		assertEquals(100, day.get().getLowPrice(), DELTA);
		assertEquals(300, day.get().getFinishPrice(), DELTA);
		assertEquals(150, day.get().getAvOpeningPrice(), DELTA);
		assertEquals(250, day.get().getAvFinishPrice(), DELTA);

		// Test findPrevious method.
		Optional<TrnFXDay> fxPreviousDay = fxDayService.findPrevious(key);
		// Check result.
		assertEquals(false, fxPreviousDay.isPresent());
		// Next hour.
		key.setRegistDate(key.getRegistDate().plusDays(1));
		// Do fine.
		fxPreviousDay = fxDayService.findPrevious(key);
		// Check result.
		assertEquals(true, fxPreviousDay.isPresent());
		assertEquals("usdjpy", fxPreviousDay.get().getTickKey().getCurrencyPair());
		assertEquals(200, fxPreviousDay.get().getOpeningPrice(), DELTA);
		assertEquals(400, fxPreviousDay.get().getHighPrice(), DELTA);
		assertEquals(100, fxPreviousDay.get().getLowPrice(), DELTA);
		assertEquals(300, fxPreviousDay.get().getFinishPrice(), DELTA);
		assertEquals(150, fxPreviousDay.get().getAvOpeningPrice(), DELTA);
		assertEquals(250, fxPreviousDay.get().getAvFinishPrice(), DELTA);
		// Another next hour.
		key.setRegistDate(key.getRegistDate().plusDays(1));
		// Do find.
		fxPreviousDay = fxDayService.findPrevious(key);
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
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxday/generate/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testGenerateFXPeriodData() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160103 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB.
		AbstractFXEntity hour = fxDayService.generateFXPeriodData(FXType.HOUR, "usdjpy", result);
		// Check result.
		assertNull(hour);

		// Get one from DB.
		AbstractFXEntity sixHour = fxDayService.generateFXPeriodData(FXType.SIXHOUR, "usdjpy", result);
		// Check result.
		assertNull(sixHour);

		// Get one from DB.
		AbstractFXEntity day = fxDayService.generateFXPeriodData(FXType.DAY, "usdjpy", result);
		// Check result.
		assertNull(day);

		// Get one from DB.
		AbstractFXEntity week = fxDayService.generateFXPeriodData(FXType.WEEK, "usdjpy", result);
		// Check result.
		assertEquals(true, week instanceof TrnFXWeek);
		assertEquals(12, week.getOpeningPrice(), DELTA);
		assertEquals(49, week.getHighPrice(), DELTA);
		assertEquals(3, week.getLowPrice(), DELTA);
		assertEquals(36, week.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity month = fxDayService.generateFXPeriodData(FXType.MONTH, "usdjpy", result);
		// Check result.
		assertEquals(true, month instanceof TrnFXMonth);
		assertEquals(12, month.getOpeningPrice(), DELTA);
		assertEquals(49, month.getHighPrice(), DELTA);
		assertEquals(1, month.getLowPrice(), DELTA);
		assertEquals(36, month.getFinishPrice(), DELTA);
	}

	@Test
	@ExpectedDatabase(value = "/data/fx/service/fxday/operation/expectedData4Insert.xml", table = "trn_fx_day", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get TrnFXTick data from DB.
		TrnFXDay fxDay = new TrnFXDay();
		fxDay.setTickKey(key);
		fxDay.setOpeningPrice(10);
		fxDay.setHighPrice(20);
		fxDay.setLowPrice(100);
		fxDay.setFinishPrice(0);
		fxDay.setAvOpeningPrice(11);
		fxDay.setAvFinishPrice(12);
		// Do insert.
		fxDayService.operation(fxDay, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxday/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxday/operation/expectedData4Update.xml", table = "trn_fx_day", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get TrnFXTick data from DB.
		TrnFXDay fxDay = fxDayService.findOne(key).get();
		fxDay.setTickKey(key);
		fxDay.setOpeningPrice(100);
		fxDay.setHighPrice(200);
		fxDay.setLowPrice(1000);
		fxDay.setFinishPrice(10);
		fxDay.setAvOpeningPrice(11);
		fxDay.setAvFinishPrice(12);
		// Do insert.
		fxDayService.operation(fxDay, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxday/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxday/operation/expectedData4Delete.xml", table = "trn_fx_day")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New TrnFXTick data.
		Optional<TrnFXDay> fxDay = fxDayService.findOne(key);
		// Delete one from DB.
		fxDayService.operation(fxDay.get(), OperationMode.DELETE);
	}
}
