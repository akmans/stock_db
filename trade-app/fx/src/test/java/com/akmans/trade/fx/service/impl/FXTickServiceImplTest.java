package com.akmans.trade.fx.service.impl;

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
import com.akmans.trade.core.enums.FXType;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.fx.service.FXTickService;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXTickRepositoryTest;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class FXTickServiceImplTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TrnFXTickRepositoryTest.class);

	private static final double DELTA = 1e-15;

	@Autowired
	private FXTickService fxTickService;
/*
	@Autowired
	private ScaleService scaleService;

	@Autowired
	private Sector17Service sector17Service;

	@Autowired
	private Sector33Service sector33Service;

	@Autowired
	private InstrumentService instrumentService;
*/
	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxtick/find/input.xml")
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
		TrnFXTick tick = fxTickService.findOne(key);
		// Check result.
		assertEquals("usdjpy", tick.getTickKey().getCurrencyPair());
		assertEquals(100, tick.getBidPrice(), DELTA);
		assertEquals(200, tick.getAskPrice(), DELTA);
		assertEquals(150, tick.getMidPrice(), DELTA);
		assertEquals(0, tick.getProcessedFlag(), DELTA);

		// Exist from DB by key.
		boolean exist = fxTickService.exist(key);
		// Check result.
		assertTrue(exist);
		key.setCurrencyPair("nzdjpy");
		// Exist from DB by key.
		exist = fxTickService.exist(key);
		// Check result.
		assertEquals(false, exist);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxtick/generate/input.xml")
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
		AbstractFXEntity hour = fxTickService.generateFXPeriodData(FXType.HOUR, "usdjpy", result);
		// Check result.
		assertEquals(true, hour instanceof TrnFXHour);
		assertEquals(100, hour.getOpeningPrice(), DELTA);
		assertEquals(112, hour.getHighPrice(), DELTA);
		assertEquals(99, hour.getLowPrice(), DELTA);
		assertEquals(111, hour.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity sixHour = fxTickService.generateFXPeriodData(FXType.SIXHOUR, "usdjpy", result);
		// Check result.
		assertEquals(true, sixHour instanceof TrnFX6Hour);
		assertEquals(100, sixHour.getOpeningPrice(), DELTA);
		assertEquals(113, sixHour.getHighPrice(), DELTA);
		assertEquals(98, sixHour.getLowPrice(), DELTA);
		assertEquals(98, sixHour.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity day = fxTickService.generateFXPeriodData(FXType.DAY, "usdjpy", result);
		// Check result.
		assertEquals(true, day instanceof TrnFXDay);
		assertEquals(100, day.getOpeningPrice(), DELTA);
		assertEquals(114, day.getHighPrice(), DELTA);
		assertEquals(97, day.getLowPrice(), DELTA);
		assertEquals(97, day.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity week = fxTickService.generateFXPeriodData(FXType.WEEK, "usdjpy", result);
		// Check result.
		assertEquals(true, week instanceof TrnFXWeek);
		assertEquals(100, week.getOpeningPrice(), DELTA);
		assertEquals(115, week.getHighPrice(), DELTA);
		assertEquals(96, week.getLowPrice(), DELTA);
		assertEquals(96, week.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity month = fxTickService.generateFXPeriodData(FXType.MONTH, "usdjpy", result);
		// Check result.
		assertEquals(true, month instanceof TrnFXMonth);
		assertEquals(100, month.getOpeningPrice(), DELTA);
		assertEquals(116, month.getHighPrice(), DELTA);
		assertEquals(95, month.getLowPrice(), DELTA);
		assertEquals(95, month.getFinishPrice(), DELTA);
	}

	@Test
	@ExpectedDatabase(value = "/data/fx/service/fxtick/operation/expectedData4Insert.xml", table = "trn_fx_tick", assertionMode = DatabaseAssertionMode.NON_STRICT)
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
		TrnFXTick tick = new TrnFXTick();
		tick.setTickKey(key);
		tick.setBidPrice(10);
		tick.setAskPrice(20);
		tick.setMidPrice(100);
		tick.setProcessedFlag(0);
		// Do insert.
		fxTickService.operation(tick, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxtick/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxtick/operation/expectedData4Update.xml", table = "trn_fx_tick", assertionMode = DatabaseAssertionMode.NON_STRICT)
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
		// New TrnFXTick data.
		TrnFXTick tick = fxTickService.findOne(key);
		tick.setBidPrice(1);
		tick.setAskPrice(2);
		tick.setMidPrice(10);
		tick.setProcessedFlag(1);
		// Do insert.
		fxTickService.operation(tick, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxtick/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxtick/operation/expectedData4Delete.xml", table = "trn_fx_tick")
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
		TrnFXTick tick = fxTickService.findOne(key);
		// Delete one from DB.
		fxTickService.operation(tick, OperationMode.DELETE);
	}
}
