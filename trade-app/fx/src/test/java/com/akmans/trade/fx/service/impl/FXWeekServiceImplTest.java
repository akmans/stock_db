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
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.fx.service.FXWeekService;
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
public class FXWeekServiceImplTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXWeekServiceImplTest.class);

	private static final double DELTA = 1e-15;

	@Autowired
	private FXWeekService fxWeekService;

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxweek/find/input.xml")
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
		Optional<TrnFXWeek> week = fxWeekService.findOne(key);
		// Check result.
		assertTrue(week.isPresent());
		assertEquals("usdjpy", week.get().getTickKey().getCurrencyPair());
		assertEquals(200, week.get().getOpeningPrice(), DELTA);
		assertEquals(400, week.get().getHighPrice(), DELTA);
		assertEquals(100, week.get().getLowPrice(), DELTA);
		assertEquals(300, week.get().getFinishPrice(), DELTA);
		assertEquals(150, week.get().getAvOpeningPrice(), DELTA);
		assertEquals(250, week.get().getAvFinishPrice(), DELTA);
	}

	@Test
	@ExpectedDatabase(value = "/data/fx/service/fxweek/operation/expectedData4Insert.xml", table = "trn_fx_week", assertionMode = DatabaseAssertionMode.NON_STRICT)
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
		TrnFXWeek fxWeek = new TrnFXWeek();
		fxWeek.setTickKey(key);
		fxWeek.setOpeningPrice(10);
		fxWeek.setHighPrice(20);
		fxWeek.setLowPrice(100);
		fxWeek.setFinishPrice(0);
		fxWeek.setAvOpeningPrice(11);
		fxWeek.setAvFinishPrice(12);
		// Do insert.
		fxWeekService.operation(fxWeek, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxweek/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxweek/operation/expectedData4Update.xml", table = "trn_fx_week", assertionMode = DatabaseAssertionMode.NON_STRICT)
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
		TrnFXWeek fxWeek = fxWeekService.findOne(key).get();
		fxWeek.setTickKey(key);
		fxWeek.setOpeningPrice(100);
		fxWeek.setHighPrice(200);
		fxWeek.setLowPrice(1000);
		fxWeek.setFinishPrice(10);
		fxWeek.setAvOpeningPrice(11);
		fxWeek.setAvFinishPrice(12);
		// Do insert.
		fxWeekService.operation(fxWeek, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxweek/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxweek/operation/expectedData4Delete.xml", table = "trn_fx_week")
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
		Optional<TrnFXWeek> fxWeek = fxWeekService.findOne(key);
		// Delete one from DB.
		fxWeekService.operation(fxWeek.get(), OperationMode.DELETE);
	}
}
