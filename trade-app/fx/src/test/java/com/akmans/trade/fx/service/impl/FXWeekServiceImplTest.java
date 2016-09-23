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
import com.akmans.trade.fx.service.FXWeekService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXWeekRepository;
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

	private static final double DELTA = 1e-15;

	@Autowired
	private FXWeekService fxWeekService;

	@Test
	public void testFindOneWithMock() {
		TrnFXWeekRepository trnFXWeekRepository = Mockito.mock(TrnFXWeekRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXWeekService fxWeekService = new FXWeekServiceImpl(trnFXWeekRepository, messageService);
		/** 1. Test found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		// Expected Week data.
		TrnFXWeek sixHour = new TrnFXWeek();
		sixHour.setTickKey(key);
		Optional<TrnFXWeek> option = Optional.of(sixHour);

		// Mockito expectations
		when(trnFXWeekRepository.findOne(any(FXTickKey.class))).thenReturn(option);
		// Execute the method being tested
		Optional<TrnFXWeek> fxWeek = fxWeekService.findOne(key);
		// Validation
		assertEquals(true, fxWeek.isPresent());
		assertEquals(sixHour, fxWeek.get());

		/** 2. Test not found */
		// Mockito expectations
		when(trnFXWeekRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		// Execute the method being tested
		fxWeek = fxWeekService.findOne(key);
		// Validation
		assertEquals(false, fxWeek.isPresent());
	}

	@Test
	public void testFindPreviousWithMock() {
		TrnFXWeekRepository trnFXWeekRepository = Mockito.mock(TrnFXWeekRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXWeekService fxWeekService = new FXWeekServiceImpl(trnFXWeekRepository, messageService);
		/** 1. Test when FXTickKey is null */
		// Execute the method being tested
		Optional<TrnFXWeek> fxWeek = fxWeekService.findPrevious(null);
		// Validation
		assertEquals(false, fxWeek.isPresent());

		/** 2. Test found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected Week data.
		TrnFXWeek sixHour = new TrnFXWeek();
		sixHour.setTickKey(key);
		Optional<TrnFXWeek> option = Optional.of(sixHour);

		// Mockito expectations
		when(trnFXWeekRepository.findPrevious(any(String.class), any(LocalDateTime.class))).thenReturn(option);
		// Execute the method being tested
		fxWeek = fxWeekService.findPrevious(key);
		// Validation
		assertEquals(true, fxWeek.isPresent());
		assertEquals(sixHour, fxWeek.get());

		/** 3. Test not found */
		// Mockito expectations
		when(trnFXWeekRepository.findPrevious(any(String.class), any(LocalDateTime.class)))
				.thenReturn(Optional.empty());
		// Execute the method being tested
		fxWeek = fxWeekService.findPrevious(key);
		// Validation
		assertEquals(false, fxWeek.isPresent());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fxweek/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFind() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
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

		// Test findPrevious method.
		Optional<TrnFXWeek> fxPreviousWeek = fxWeekService.findPrevious(key);
		// Check result.
		assertEquals(false, fxPreviousWeek.isPresent());
		// Next hour.
		key.setRegistDate(key.getRegistDate().plusWeeks(1));
		// Do fine.
		fxPreviousWeek = fxWeekService.findPrevious(key);
		// Check result.
		assertEquals(true, fxPreviousWeek.isPresent());
		assertEquals("usdjpy", fxPreviousWeek.get().getTickKey().getCurrencyPair());
		assertEquals(200, fxPreviousWeek.get().getOpeningPrice(), DELTA);
		assertEquals(400, fxPreviousWeek.get().getHighPrice(), DELTA);
		assertEquals(100, fxPreviousWeek.get().getLowPrice(), DELTA);
		assertEquals(300, fxPreviousWeek.get().getFinishPrice(), DELTA);
		assertEquals(150, fxPreviousWeek.get().getAvOpeningPrice(), DELTA);
		assertEquals(250, fxPreviousWeek.get().getAvFinishPrice(), DELTA);
		// Another next hour.
		key.setRegistDate(key.getRegistDate().plusWeeks(1));
		// Do find.
		fxPreviousWeek = fxWeekService.findPrevious(key);
		// Check result.
		assertEquals(true, fxPreviousWeek.isPresent());
		assertEquals("usdjpy", fxPreviousWeek.get().getTickKey().getCurrencyPair());
		assertEquals(20, fxPreviousWeek.get().getOpeningPrice(), DELTA);
		assertEquals(40, fxPreviousWeek.get().getHighPrice(), DELTA);
		assertEquals(10, fxPreviousWeek.get().getLowPrice(), DELTA);
		assertEquals(30, fxPreviousWeek.get().getFinishPrice(), DELTA);
		assertEquals(15, fxPreviousWeek.get().getAvOpeningPrice(), DELTA);
		assertEquals(25, fxPreviousWeek.get().getAvFinishPrice(), DELTA);
	}

	@Test
	public void testOperation4InsertWithMock() throws Exception {
		TrnFXWeekRepository trnFXWeekRepository = Mockito.mock(TrnFXWeekRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXWeekService fxWeekService = new FXWeekServiceImpl(trnFXWeekRepository, messageService);
		/** 1. Test insert success */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		// Get TrnFXTick data from DB.
		TrnFXWeek sixHour = new TrnFXWeek();
		sixHour.setTickKey(key);

		// Mockito expectations
		when(trnFXWeekRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(trnFXWeekRepository.save(any(TrnFXWeek.class))).thenReturn(sixHour);
		// Execute the method being tested
		TrnFXWeek fxWeek = fxWeekService.operation(sixHour, OperationMode.NEW);
		// Validation
		assertEquals(sixHour, fxWeek);

		/** 2. Test key duplicated */
		// Mockito expectations
		when(trnFXWeekRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(sixHour));
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxWeekService.operation(sixHour, OperationMode.NEW)).isInstanceOf(TradeException.class)
				.hasMessage("Error!");
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
		key.setRegistDate(dateTime);
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
	public void testOperation4UpdateWithMock() throws Exception {
		TrnFXWeekRepository trnFXWeekRepository = Mockito.mock(TrnFXWeekRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXWeekService fxWeekService = new FXWeekServiceImpl(trnFXWeekRepository, messageService);
		/** 1. Test updated data not found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected TrnFXTick data.
		TrnFXWeek sixHour = new TrnFXWeek();
		sixHour.setTickKey(key);
		sixHour.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(trnFXWeekRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxWeekService.operation(sixHour, OperationMode.EDIT))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 2. Test updated data inconsistent */
		// Mockito expectations
		when(trnFXWeekRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(sixHour));
		// Expected TrnFXTick data.
		TrnFXWeek newSixHour = new TrnFXWeek();
		// Copy all data.
		BeanUtils.copyProperties(sixHour, newSixHour);
		newSixHour.setUpdatedDate(ZonedDateTime.now());
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxWeekService.operation(newSixHour, OperationMode.EDIT))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test update success */
		// Mockito expectations
		when(trnFXWeekRepository.save(any(TrnFXWeek.class))).thenReturn(sixHour);
		// Execute the method being tested
		TrnFXWeek fxWeek = fxWeekService.operation(sixHour, OperationMode.EDIT);
		// Validation
		assertEquals(sixHour, fxWeek);
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
		key.setRegistDate(dateTime);
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
	public void testOperation4DeleteWithMock() throws Exception {
		TrnFXWeekRepository trnFXWeekRepository = Mockito.mock(TrnFXWeekRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXWeekService fxWeekService = new FXWeekServiceImpl(trnFXWeekRepository, messageService);
		/** 1. Test deleted data not found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected TrnFXTick data.
		TrnFXWeek sixHour = new TrnFXWeek();
		sixHour.setTickKey(key);
		sixHour.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(trnFXWeekRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxWeekService.operation(sixHour, OperationMode.DELETE))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 2. Test deleted data inconsistent */
		// Mockito expectations
		when(trnFXWeekRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(sixHour));
		// Expected TrnFXTick data.
		TrnFXWeek newWeek = new TrnFXWeek();
		// Copy all data.
		BeanUtils.copyProperties(sixHour, newWeek);
		newWeek.setUpdatedDate(ZonedDateTime.now());
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxWeekService.operation(newWeek, OperationMode.DELETE))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test deleted success */
		// Mockito expectations
		doNothing().when(trnFXWeekRepository).delete(any(TrnFXWeek.class));
		// Execute the method being tested
		TrnFXWeek fxWeek = fxWeekService.operation(sixHour, OperationMode.DELETE);
		// Validation
		assertNull(fxWeek);
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
		key.setRegistDate(dateTime);
		// New TrnFXTick data.
		Optional<TrnFXWeek> fxWeek = fxWeekService.findOne(key);
		// Delete one from DB.
		fxWeekService.operation(fxWeek.get(), OperationMode.DELETE);
	}
}
