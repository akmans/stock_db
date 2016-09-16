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
import com.akmans.trade.fx.service.FXMonthService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
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
public class FXMonthServiceImplTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXMonthServiceImplTest.class);

	private static final double DELTA = 1e-15;

	@Autowired
	private FXMonthService fxMonthService;

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxmonth/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFind() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160101 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB by key.
		Optional<TrnFXMonth> month = fxMonthService.findOne(key);
		// Check result.
		assertTrue(month.isPresent());
		assertEquals("usdjpy", month.get().getTickKey().getCurrencyPair());
		assertEquals(200, month.get().getOpeningPrice(), DELTA);
		assertEquals(400, month.get().getHighPrice(), DELTA);
		assertEquals(100, month.get().getLowPrice(), DELTA);
		assertEquals(300, month.get().getFinishPrice(), DELTA);
		assertEquals(150, month.get().getAvOpeningPrice(), DELTA);
		assertEquals(250, month.get().getAvFinishPrice(), DELTA);
	}

	@Test
	@ExpectedDatabase(value = "/data/fx/service/fxmonth/operation/expectedData4Insert.xml", table = "trn_fx_month", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160101 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get TrnFXTick data from DB.
		TrnFXMonth fxMonth = new TrnFXMonth();
		fxMonth.setTickKey(key);
		fxMonth.setOpeningPrice(10);
		fxMonth.setHighPrice(20);
		fxMonth.setLowPrice(100);
		fxMonth.setFinishPrice(0);
		fxMonth.setAvOpeningPrice(11);
		fxMonth.setAvFinishPrice(12);
		// Do insert.
		fxMonthService.operation(fxMonth, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxmonth/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxmonth/operation/expectedData4Update.xml", table = "trn_fx_month", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160101 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get TrnFXTick data from DB.
		TrnFXMonth fxMonth = fxMonthService.findOne(key).get();
		fxMonth.setTickKey(key);
		fxMonth.setOpeningPrice(100);
		fxMonth.setHighPrice(200);
		fxMonth.setLowPrice(1000);
		fxMonth.setFinishPrice(10);
		fxMonth.setAvOpeningPrice(11);
		fxMonth.setAvFinishPrice(12);
		// Do insert.
		fxMonthService.operation(fxMonth, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxmonth/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxmonth/operation/expectedData4Delete.xml", table = "trn_fx_month")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160101 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New TrnFXTick data.
		Optional<TrnFXMonth> fxMonth = fxMonthService.findOne(key);
		// Delete one from DB.
		fxMonthService.operation(fxMonth.get(), OperationMode.DELETE);
	}
}
