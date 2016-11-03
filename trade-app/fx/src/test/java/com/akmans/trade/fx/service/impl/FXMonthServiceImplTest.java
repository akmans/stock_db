package com.akmans.trade.fx.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.fx.service.FXDayService;
import com.akmans.trade.fx.service.FXMonthService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXMonthRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class FXMonthServiceImplTest {

	private static final double DELTA = 1e-15;

	@Autowired
	private FXMonthService fxMonthService;

	@Test
	public void testFindOneWithMock() {
		FXDayService fxDayService = Mockito.mock(FXDayService.class);
		TrnFXMonthRepository trnFXMonthRepository = Mockito.mock(TrnFXMonthRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXMonthService fxMonthService = new FXMonthServiceImpl(fxDayService, trnFXMonthRepository, messageService);
		/** 1. Test found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		// Expected Month data.
		TrnFXMonth sixHour = new TrnFXMonth();
		sixHour.setTickKey(key);
		Optional<TrnFXMonth> option = Optional.of(sixHour);

		// Mockito expectations
		when(trnFXMonthRepository.findOne(any(FXTickKey.class))).thenReturn(option);
		// Execute the method being tested
		Optional<TrnFXMonth> fxMonth = fxMonthService.findOne(key);
		// Validation
		assertEquals(true, fxMonth.isPresent());
		assertEquals(sixHour, fxMonth.get());

		/** 2. Test not found */
		// Mockito expectations
		when(trnFXMonthRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		// Execute the method being tested
		fxMonth = fxMonthService.findOne(key);
		// Validation
		assertEquals(false, fxMonth.isPresent());
	}

	@Test
	public void testFindPreviousWithMock() {
		FXDayService fxDayService = Mockito.mock(FXDayService.class);
		TrnFXMonthRepository trnFXMonthRepository = Mockito.mock(TrnFXMonthRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXMonthService fxMonthService = new FXMonthServiceImpl(fxDayService, trnFXMonthRepository, messageService);
		/** 1. Test when FXTickKey is null */
		// Execute the method being tested
		Optional<TrnFXMonth> fxMonth = fxMonthService.findPrevious(null);
		// Validation
		assertEquals(false, fxMonth.isPresent());

		/** 2. Test found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected Month data.
		TrnFXMonth sixHour = new TrnFXMonth();
		sixHour.setTickKey(key);
		Optional<TrnFXMonth> option = Optional.of(sixHour);

		// Mockito expectations
		when(trnFXMonthRepository.findPrevious(any(String.class), any(LocalDateTime.class))).thenReturn(option);
		// Execute the method being tested
		fxMonth = fxMonthService.findPrevious(key);
		// Validation
		assertEquals(true, fxMonth.isPresent());
		assertEquals(sixHour, fxMonth.get());

		/** 3. Test not found */
		// Mockito expectations
		when(trnFXMonthRepository.findPrevious(any(String.class), any(LocalDateTime.class)))
				.thenReturn(Optional.empty());
		// Execute the method being tested
		fxMonth = fxMonthService.findPrevious(key);
		// Validation
		assertEquals(false, fxMonth.isPresent());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxmonth/find/input.xml")
	public void testFind() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160101 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
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

		// Test findPrevious method.
		Optional<TrnFXMonth> fxPreviousMonth = fxMonthService.findPrevious(key);
		// Check result.
		assertEquals(false, fxPreviousMonth.isPresent());
		// Next hour.
		key.setRegistDate(key.getRegistDate().plusMonths(1));
		// Do fine.
		fxPreviousMonth = fxMonthService.findPrevious(key);
		// Check result.
		assertEquals(true, fxPreviousMonth.isPresent());
		assertEquals("usdjpy", fxPreviousMonth.get().getTickKey().getCurrencyPair());
		assertEquals(200, fxPreviousMonth.get().getOpeningPrice(), DELTA);
		assertEquals(400, fxPreviousMonth.get().getHighPrice(), DELTA);
		assertEquals(100, fxPreviousMonth.get().getLowPrice(), DELTA);
		assertEquals(300, fxPreviousMonth.get().getFinishPrice(), DELTA);
		assertEquals(150, fxPreviousMonth.get().getAvOpeningPrice(), DELTA);
		assertEquals(250, fxPreviousMonth.get().getAvFinishPrice(), DELTA);
		// Another next hour.
		key.setRegistDate(key.getRegistDate().plusMonths(1));
		// Do find.
		fxPreviousMonth = fxMonthService.findPrevious(key);
		// Check result.
		assertEquals(true, fxPreviousMonth.isPresent());
		assertEquals("usdjpy", fxPreviousMonth.get().getTickKey().getCurrencyPair());
		assertEquals(20, fxPreviousMonth.get().getOpeningPrice(), DELTA);
		assertEquals(40, fxPreviousMonth.get().getHighPrice(), DELTA);
		assertEquals(10, fxPreviousMonth.get().getLowPrice(), DELTA);
		assertEquals(30, fxPreviousMonth.get().getFinishPrice(), DELTA);
		assertEquals(15, fxPreviousMonth.get().getAvOpeningPrice(), DELTA);
		assertEquals(25, fxPreviousMonth.get().getAvFinishPrice(), DELTA);
	}

	@Test
	public void testOperation4InsertWithMock() throws Exception {
		FXDayService fxDayService = Mockito.mock(FXDayService.class);
		TrnFXMonthRepository trnFXMonthRepository = Mockito.mock(TrnFXMonthRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXMonthService fxMonthService = new FXMonthServiceImpl(fxDayService, trnFXMonthRepository, messageService);
		/** 1. Test insert success */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		// Get TrnFXTick data from DB.
		TrnFXMonth sixHour = new TrnFXMonth();
		sixHour.setTickKey(key);

		// Mockito expectations
		when(trnFXMonthRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(trnFXMonthRepository.save(any(TrnFXMonth.class))).thenReturn(sixHour);
		// Execute the method being tested
		TrnFXMonth fxMonth = fxMonthService.operation(sixHour, OperationMode.NEW);
		// Validation
		assertEquals(sixHour, fxMonth);

		/** 2. Test key duplicated */
		// Mockito expectations
		when(trnFXMonthRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(sixHour));
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxMonthService.operation(sixHour, OperationMode.NEW))
				.isInstanceOf(TradeException.class).hasMessage("Error!");
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@ExpectedDatabase(value = "/data/fx/service/fxmonth/operation/expectedData4Insert.xml", table = "trn_fx_month", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testOperation4Insert() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160101 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
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
	public void testOperation4UpdateWithMock() throws Exception {
		FXDayService fxDayService = Mockito.mock(FXDayService.class);
		TrnFXMonthRepository trnFXMonthRepository = Mockito.mock(TrnFXMonthRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXMonthService fxMonthService = new FXMonthServiceImpl(fxDayService, trnFXMonthRepository, messageService);
		/** 1. Test updated data not found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected TrnFXTick data.
		TrnFXMonth sixHour = new TrnFXMonth();
		sixHour.setTickKey(key);
		sixHour.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(trnFXMonthRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxMonthService.operation(sixHour, OperationMode.EDIT))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 2. Test updated data inconsistent */
		// Mockito expectations
		when(trnFXMonthRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(sixHour));
		// Expected TrnFXTick data.
		TrnFXMonth newSixHour = new TrnFXMonth();
		// Copy all data.
		BeanUtils.copyProperties(sixHour, newSixHour);
		newSixHour.setUpdatedDate(ZonedDateTime.now().plusHours(1l));
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxMonthService.operation(newSixHour, OperationMode.EDIT))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test update success */
		// Mockito expectations
		when(trnFXMonthRepository.save(any(TrnFXMonth.class))).thenReturn(sixHour);
		// Execute the method being tested
		TrnFXMonth fxMonth = fxMonthService.operation(sixHour, OperationMode.EDIT);
		// Validation
		assertEquals(sixHour, fxMonth);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxmonth/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxmonth/operation/expectedData4Update.xml", table = "trn_fx_month", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testOperation4Update() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160101 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
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
	public void testOperation4DeleteWithMock() throws Exception {
		FXDayService fxDayService = Mockito.mock(FXDayService.class);
		TrnFXMonthRepository trnFXMonthRepository = Mockito.mock(TrnFXMonthRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXMonthService fxMonthService = new FXMonthServiceImpl(fxDayService, trnFXMonthRepository, messageService);
		/** 1. Test deleted data not found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected TrnFXTick data.
		TrnFXMonth sixHour = new TrnFXMonth();
		sixHour.setTickKey(key);
		sixHour.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(trnFXMonthRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxMonthService.operation(sixHour, OperationMode.DELETE))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 2. Test deleted data inconsistent */
		// Mockito expectations
		when(trnFXMonthRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(sixHour));
		// Expected TrnFXTick data.
		TrnFXMonth newMonth = new TrnFXMonth();
		// Copy all data.
		BeanUtils.copyProperties(sixHour, newMonth);
		newMonth.setUpdatedDate(ZonedDateTime.now().plusHours(1l));
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxMonthService.operation(newMonth, OperationMode.DELETE))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test deleted success */
		// Mockito expectations
		doNothing().when(trnFXMonthRepository).delete(any(TrnFXMonth.class));
		// Execute the method being tested
		TrnFXMonth fxMonth = fxMonthService.operation(sixHour, OperationMode.DELETE);
		// Validation
		assertNull(fxMonth);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxmonth/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxmonth/operation/expectedData4Delete.xml", table = "trn_fx_month")
	public void testOperation4Delete() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160101 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
		// New TrnFXTick data.
		Optional<TrnFXMonth> fxMonth = fxMonthService.findOne(key);
		// Delete one from DB.
		fxMonthService.operation(fxMonth.get(), OperationMode.DELETE);
	}
}
