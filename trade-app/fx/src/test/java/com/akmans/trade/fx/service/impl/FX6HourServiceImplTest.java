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
import com.akmans.trade.fx.service.FX6HourService;
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
public class FX6HourServiceImplTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FX6HourServiceImplTest.class);

	private static final double DELTA = 1e-15;

	@Autowired
	private FX6HourService fx6HourService;

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fx6hour/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFind() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB by key.
		Optional<TrnFX6Hour> sixhour = fx6HourService.findOne(key);
		// Check result.
		assertTrue(sixhour.isPresent());
		assertEquals("usdjpy", sixhour.get().getTickKey().getCurrencyPair());
		assertEquals(200, sixhour.get().getOpeningPrice(), DELTA);
		assertEquals(400, sixhour.get().getHighPrice(), DELTA);
		assertEquals(100, sixhour.get().getLowPrice(), DELTA);
		assertEquals(300, sixhour.get().getFinishPrice(), DELTA);
		assertEquals(150, sixhour.get().getAvOpeningPrice(), DELTA);
		assertEquals(250, sixhour.get().getAvFinishPrice(), DELTA);
	}

	@Test
	@ExpectedDatabase(value = "/data/fx/service/fx6hour/operation/expectedData4Insert.xml", table = "trn_fx_6hour", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get TrnFXTick data from DB.
		TrnFX6Hour fx6Hour = new TrnFX6Hour();
		fx6Hour.setTickKey(key);
		fx6Hour.setOpeningPrice(10);
		fx6Hour.setHighPrice(20);
		fx6Hour.setLowPrice(100);
		fx6Hour.setFinishPrice(0);
		fx6Hour.setAvOpeningPrice(11);
		fx6Hour.setAvFinishPrice(12);
		// Do insert.
		fx6HourService.operation(fx6Hour, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fx6hour/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/fx/service/fx6hour/operation/expectedData4Update.xml", table = "trn_fx_6hour", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get TrnFXTick data from DB.
		TrnFX6Hour fx6Hour = fx6HourService.findOne(key).get();
		fx6Hour.setTickKey(key);
		fx6Hour.setOpeningPrice(100);
		fx6Hour.setHighPrice(200);
		fx6Hour.setLowPrice(1000);
		fx6Hour.setFinishPrice(10);
		fx6Hour.setAvOpeningPrice(11);
		fx6Hour.setAvFinishPrice(12);
		// Do insert.
		fx6HourService.operation(fx6Hour, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fx6hour/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/fx/service/fx6hour/operation/expectedData4Delete.xml", table = "trn_fx_6hour")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New TrnFXTick data.
		Optional<TrnFX6Hour> fx6Hour = fx6HourService.findOne(key);
		// Delete one from DB.
		fx6HourService.operation(fx6Hour.get(), OperationMode.DELETE);
	}
}
