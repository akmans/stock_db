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
import com.akmans.trade.fx.service.FX6HourService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFX6HourRepository;
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

	private static final double DELTA = 1e-15;

	@Autowired
	private FX6HourService fx6HourService;

	@Test
	public void testFindOneWithMock() throws Exception {
		TrnFX6HourRepository trnFX6HourRepository = Mockito.mock(TrnFX6HourRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FX6HourService fx6HourService = new FX6HourServiceImpl(trnFX6HourRepository, messageService);
		/** 1. Test found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		// Expected 6Hour data.
		TrnFX6Hour sixHour = new TrnFX6Hour();
		sixHour.setTickKey(key);
		Optional<TrnFX6Hour> option = Optional.of(sixHour);

		// Mockito expectations
		when(trnFX6HourRepository.findOne(any(FXTickKey.class))).thenReturn(option);
		// Execute the method being tested
		Optional<TrnFX6Hour> fx6Hour = fx6HourService.findOne(key);
		// Validation
		assertEquals(true, fx6Hour.isPresent());
		assertEquals(sixHour, fx6Hour.get());

		/** 2. Test not found */
		// Mockito expectations
		when(trnFX6HourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		// Execute the method being tested
		fx6Hour = fx6HourService.findOne(key);
		// Validation
		assertEquals(false, fx6Hour.isPresent());
	}

	@Test
	public void testFindPreviousWithMock() throws Exception {
		TrnFX6HourRepository trnFX6HourRepository = Mockito.mock(TrnFX6HourRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FX6HourService fx6HourService = new FX6HourServiceImpl(trnFX6HourRepository, messageService);
		/** 1. Test when FXTickKey is null */
		// Execute the method being tested
		Optional<TrnFX6Hour> fx6Hour = fx6HourService.findPrevious(null);
		// Validation
		assertEquals(false, fx6Hour.isPresent());

		/** 2. Test found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected 6Hour data.
		TrnFX6Hour sixHour = new TrnFX6Hour();
		sixHour.setTickKey(key);
		Optional<TrnFX6Hour> option = Optional.of(sixHour);

		// Mockito expectations
		when(trnFX6HourRepository.findPrevious(any(String.class), any(LocalDateTime.class))).thenReturn(option);
		// Execute the method being tested
		fx6Hour = fx6HourService.findPrevious(key);
		// Validation
		assertEquals(true, fx6Hour.isPresent());
		assertEquals(sixHour, fx6Hour.get());

		/** 3. Test not found */
		// Mockito expectations
		when(trnFX6HourRepository.findPrevious(any(String.class), any(LocalDateTime.class)))
				.thenReturn(Optional.empty());
		// Execute the method being tested
		fx6Hour = fx6HourService.findPrevious(key);
		// Validation
		assertEquals(false, fx6Hour.isPresent());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/service/fx6hour/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFind() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:00:00.000", formatter);
		key.setRegistDate(dateTime);
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

		// Test findPrevious method.
		Optional<TrnFX6Hour> fxPrevious6Hour = fx6HourService.findPrevious(key);
		// Check result.
		assertEquals(false, fxPrevious6Hour.isPresent());
		// Next hour.
		key.setRegistDate(key.getRegistDate().plusHours(6));
		// Do fine.
		fxPrevious6Hour = fx6HourService.findPrevious(key);
		// Check result.
		assertEquals(true, fxPrevious6Hour.isPresent());
		assertEquals("usdjpy", fxPrevious6Hour.get().getTickKey().getCurrencyPair());
		assertEquals(200, fxPrevious6Hour.get().getOpeningPrice(), DELTA);
		assertEquals(400, fxPrevious6Hour.get().getHighPrice(), DELTA);
		assertEquals(100, fxPrevious6Hour.get().getLowPrice(), DELTA);
		assertEquals(300, fxPrevious6Hour.get().getFinishPrice(), DELTA);
		assertEquals(150, fxPrevious6Hour.get().getAvOpeningPrice(), DELTA);
		assertEquals(250, fxPrevious6Hour.get().getAvFinishPrice(), DELTA);
		// Another next hour.
		key.setRegistDate(key.getRegistDate().plusHours(6));
		// Do find.
		fxPrevious6Hour = fx6HourService.findPrevious(key);
		// Check result.
		assertEquals(true, fxPrevious6Hour.isPresent());
		assertEquals("usdjpy", fxPrevious6Hour.get().getTickKey().getCurrencyPair());
		assertEquals(20, fxPrevious6Hour.get().getOpeningPrice(), DELTA);
		assertEquals(40, fxPrevious6Hour.get().getHighPrice(), DELTA);
		assertEquals(10, fxPrevious6Hour.get().getLowPrice(), DELTA);
		assertEquals(30, fxPrevious6Hour.get().getFinishPrice(), DELTA);
		assertEquals(15, fxPrevious6Hour.get().getAvOpeningPrice(), DELTA);
		assertEquals(25, fxPrevious6Hour.get().getAvFinishPrice(), DELTA);
	}

	@Test
	public void testOperation4InsertWithMock() throws Exception {
		TrnFX6HourRepository trnFX6HourRepository = Mockito.mock(TrnFX6HourRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FX6HourService fx6HourService = new FX6HourServiceImpl(trnFX6HourRepository, messageService);
		/** 1. Test insert success */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		// Get TrnFXTick data from DB.
		TrnFX6Hour sixHour = new TrnFX6Hour();
		sixHour.setTickKey(key);

		// Mockito expectations
		when(trnFX6HourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(trnFX6HourRepository.save(any(TrnFX6Hour.class))).thenReturn(sixHour);
		// Execute the method being tested
		TrnFX6Hour fx6Hour = fx6HourService.operation(sixHour, OperationMode.NEW);
		// Validation
		assertEquals(sixHour, fx6Hour);

		/** 2. Test key duplicated */
		// Mockito expectations
		when(trnFX6HourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(sixHour));
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fx6HourService.operation(sixHour, OperationMode.NEW))
				.isInstanceOf(TradeException.class).hasMessage("Error!");
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
		key.setRegistDate(dateTime);
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
	public void testOperation4UpdateWithMock() throws Exception {
		TrnFX6HourRepository trnFX6HourRepository = Mockito.mock(TrnFX6HourRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FX6HourService fx6HourService = new FX6HourServiceImpl(trnFX6HourRepository, messageService);
		/** 1. Test updated data not found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected TrnFXTick data.
		TrnFX6Hour sixHour = new TrnFX6Hour();
		sixHour.setTickKey(key);
		sixHour.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(trnFX6HourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fx6HourService.operation(sixHour, OperationMode.EDIT))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 2. Test updated data inconsistent */
		// Mockito expectations
		when(trnFX6HourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(sixHour));
		// Expected TrnFXTick data.
		TrnFX6Hour newSixHour = new TrnFX6Hour();
		// Copy all data.
		BeanUtils.copyProperties(sixHour, newSixHour);
		newSixHour.setUpdatedDate(ZonedDateTime.now());
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fx6HourService.operation(newSixHour, OperationMode.EDIT))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test update success */
		// Mockito expectations
		when(trnFX6HourRepository.save(any(TrnFX6Hour.class))).thenReturn(sixHour);
		// Execute the method being tested
		TrnFX6Hour fx6Hour = fx6HourService.operation(sixHour, OperationMode.EDIT);
		// Validation
		assertEquals(sixHour, fx6Hour);
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
		key.setRegistDate(dateTime);
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
	public void testOperation4DeleteWithMock() throws Exception {
		TrnFX6HourRepository trnFX6HourRepository = Mockito.mock(TrnFX6HourRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FX6HourService fx6HourService = new FX6HourServiceImpl(trnFX6HourRepository, messageService);
		/** 1. Test deleted data not found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected TrnFXTick data.
		TrnFX6Hour sixHour = new TrnFX6Hour();
		sixHour.setTickKey(key);
		sixHour.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(trnFX6HourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fx6HourService.operation(sixHour, OperationMode.DELETE))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 2. Test deleted data inconsistent */
		// Mockito expectations
		when(trnFX6HourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(sixHour));
		// Expected TrnFXTick data.
		TrnFX6Hour newSixHour = new TrnFX6Hour();
		// Copy all data.
		BeanUtils.copyProperties(sixHour, newSixHour);
		newSixHour.setUpdatedDate(ZonedDateTime.now());
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fx6HourService.operation(newSixHour, OperationMode.DELETE))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test deleted success */
		// Mockito expectations
		doNothing().when(trnFX6HourRepository).delete(any(TrnFX6Hour.class));
		// Execute the method being tested
		TrnFX6Hour fx6Hour = fx6HourService.operation(sixHour, OperationMode.DELETE);
		// Validation
		assertNull(fx6Hour);
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
		key.setRegistDate(dateTime);
		// New TrnFXTick data.
		Optional<TrnFX6Hour> fx6Hour = fx6HourService.findOne(key);
		// Delete one from DB.
		fx6HourService.operation(fx6Hour.get(), OperationMode.DELETE);
	}
}