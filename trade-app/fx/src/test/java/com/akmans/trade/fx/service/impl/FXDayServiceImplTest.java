package com.akmans.trade.fx.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
import com.akmans.trade.core.enums.FXType;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.fx.service.FXDayService;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXDayRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class FXDayServiceImplTest {

	private static final double DELTA = 1e-15;

	@Autowired
	private FXDayService fxDayService;

	@Test
	public void testFindOneWithMock() {
		TrnFXDayRepository trnFXDayRepository = Mockito.mock(TrnFXDayRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXDayService fxDayService = new FXDayServiceImpl(trnFXDayRepository, messageService);
		/** 1. Test found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		// Expected Day data.
		TrnFXDay day = new TrnFXDay();
		day.setTickKey(key);
		Optional<TrnFXDay> option = Optional.of(day);

		// Mockito expectations
		when(trnFXDayRepository.findOne(any(FXTickKey.class))).thenReturn(option);
		// Execute the method being tested
		Optional<TrnFXDay> fxDay = fxDayService.findOne(key);
		// Validation
		assertEquals(true, fxDay.isPresent());
		assertEquals(day, fxDay.get());

		/** 2. Test not found */
		// Mockito expectations
		when(trnFXDayRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		// Execute the method being tested
		fxDay = fxDayService.findOne(key);
		// Validation
		assertEquals(false, fxDay.isPresent());
	}

	@Test
	public void testFindPreviousWithMock() {
		TrnFXDayRepository trnFXDayRepository = Mockito.mock(TrnFXDayRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXDayService fxDayService = new FXDayServiceImpl(trnFXDayRepository, messageService);
		/** 1. Test when FXTickKey is null */
		// Execute the method being tested
		Optional<TrnFXDay> fxDay = fxDayService.findPrevious(null);
		// Validation
		assertEquals(false, fxDay.isPresent());

		/** 2. Test found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected Day data.
		TrnFXDay day = new TrnFXDay();
		day.setTickKey(key);
		Optional<TrnFXDay> option = Optional.of(day);

		// Mockito expectations
		when(trnFXDayRepository.findPrevious(any(String.class), any(LocalDateTime.class))).thenReturn(option);
		// Execute the method being tested
		fxDay = fxDayService.findPrevious(key);
		// Validation
		assertEquals(true, fxDay.isPresent());
		assertEquals(day, fxDay.get());

		/** 3. Test not found */
		// Mockito expectations
		when(trnFXDayRepository.findPrevious(any(String.class), any(LocalDateTime.class))).thenReturn(Optional.empty());
		// Execute the method being tested
		fxDay = fxDayService.findPrevious(key);
		// Validation
		assertEquals(false, fxDay.isPresent());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxday/find/input.xml")
	public void testFind() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
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
	public void testGenerateFXPeriodDataWithMock() throws Exception {
		TrnFXDayRepository trnFXDayRepository = Mockito.mock(TrnFXDayRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXDayService fxDayService = new FXDayServiceImpl(trnFXDayRepository, messageService);
		/** 1. Test FXType is HOUR */
		AbstractFXEntity entity = fxDayService.generateFXPeriodData(FXType.HOUR, "usdjpy", LocalDateTime.now());
		// Validation
		assertNull(entity);

		/** 2. Test FXType is SIXHOUR */
		entity = fxDayService.generateFXPeriodData(FXType.SIXHOUR, "usdjpy", LocalDateTime.now());
		// Validation
		assertNull(entity);

		/** 3. Test FXType is DAY */
		entity = fxDayService.generateFXPeriodData(FXType.DAY, "usdjpy", LocalDateTime.now());
		// Validation
		assertNull(entity);

		/** 4. Test FXType is WEEK */
		List<TrnFXDay> days = new ArrayList<TrnFXDay>();
		TrnFXDay day0 = new TrnFXDay();
		day0.setOpeningPrice(2);
		day0.setHighPrice(4);
		day0.setLowPrice(1);
		day0.setFinishPrice(3);
		days.add(day0);
		TrnFXDay day1 = new TrnFXDay();
		day1.setOpeningPrice(20);
		day1.setHighPrice(40);
		day1.setLowPrice(10);
		day1.setFinishPrice(30);
		days.add(day1);
		TrnFXDay day2 = new TrnFXDay();
		day2.setOpeningPrice(200);
		day2.setHighPrice(400);
		day2.setLowPrice(100);
		day2.setFinishPrice(300);
		days.add(day2);
		// Mockito expectations
		when(trnFXDayRepository.findFXDayInPeriod(any(String.class), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(days);
		entity = fxDayService.generateFXPeriodData(FXType.WEEK, "usdjpy", LocalDateTime.now());
		// Validation
		assertNotNull(entity);
		assertEquals(true, entity instanceof TrnFXWeek);
		assertEquals(2, entity.getOpeningPrice(), DELTA);
		assertEquals(400, entity.getHighPrice(), DELTA);
		assertEquals(1, entity.getLowPrice(), DELTA);
		assertEquals(300, entity.getFinishPrice(), DELTA);

		/** 5. Test FXType is WEEK */
		entity = fxDayService.generateFXPeriodData(FXType.MONTH, "usdjpy", LocalDateTime.now());
		// Validation
		assertNotNull(entity);
		assertEquals(true, entity instanceof TrnFXMonth);
		assertEquals(2, entity.getOpeningPrice(), DELTA);
		assertEquals(400, entity.getHighPrice(), DELTA);
		assertEquals(1, entity.getLowPrice(), DELTA);
		assertEquals(300, entity.getFinishPrice(), DELTA);

		/** 6. Test FXDay data not found */
		when(trnFXDayRepository.findFXDayInPeriod(any(String.class), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(null);
		entity = fxDayService.generateFXPeriodData(FXType.WEEK, "usdjpy", LocalDateTime.now());
		// Validation
		assertNull(entity);
		when(trnFXDayRepository.findFXDayInPeriod(any(String.class), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(new ArrayList<TrnFXDay>());
		entity = fxDayService.generateFXPeriodData(FXType.WEEK, "usdjpy", LocalDateTime.now());
		// Validation
		assertNull(entity);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxday/generate/input.xml")
	public void testGenerateFXPeriodData() throws Exception {
		// New DateTime
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160103 00:00:00.000", formatter);
		// Get one from DB.
		AbstractFXEntity hour = fxDayService.generateFXPeriodData(FXType.HOUR, "usdjpy", dateTime);
		// Check result.
		assertNull(hour);

		// Get one from DB.
		AbstractFXEntity sixHour = fxDayService.generateFXPeriodData(FXType.SIXHOUR, "usdjpy", dateTime);
		// Check result.
		assertNull(sixHour);

		// Get one from DB.
		AbstractFXEntity day = fxDayService.generateFXPeriodData(FXType.DAY, "usdjpy", dateTime);
		// Check result.
		assertNull(day);

		// Get one from DB.
		AbstractFXEntity week = fxDayService.generateFXPeriodData(FXType.WEEK, "usdjpy", dateTime);
		// Check result.
		assertEquals(true, week instanceof TrnFXWeek);
		assertEquals(12, week.getOpeningPrice(), DELTA);
		assertEquals(49, week.getHighPrice(), DELTA);
		assertEquals(3, week.getLowPrice(), DELTA);
		assertEquals(36, week.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity month = fxDayService.generateFXPeriodData(FXType.MONTH, "usdjpy", dateTime);
		// Check result.
		assertEquals(true, month instanceof TrnFXMonth);
		assertEquals(12, month.getOpeningPrice(), DELTA);
		assertEquals(49, month.getHighPrice(), DELTA);
		assertEquals(1, month.getLowPrice(), DELTA);
		assertEquals(36, month.getFinishPrice(), DELTA);
	}

	@Test
	public void testOperation4InsertWithMock() throws Exception {
		TrnFXDayRepository trnFXDayRepository = Mockito.mock(TrnFXDayRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXDayService fxDayService = new FXDayServiceImpl(trnFXDayRepository, messageService);
		/** 1. Test insert success */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		// Get TrnFXTick data from DB.
		TrnFXDay day = new TrnFXDay();
		day.setTickKey(key);

		// Mockito expectations
		when(trnFXDayRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(trnFXDayRepository.save(any(TrnFXDay.class))).thenReturn(day);
		// Execute the method being tested
		TrnFXDay fxDay = fxDayService.operation(day, OperationMode.NEW);
		// Validation
		assertEquals(day, fxDay);

		/** 2. Test key duplicated */
		// Mockito expectations
		when(trnFXDayRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(day));
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxDayService.operation(day, OperationMode.NEW)).isInstanceOf(TradeException.class)
				.hasMessage("Error!");
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@ExpectedDatabase(value = "/data/fx/service/fxday/operation/expectedData4Insert.xml", table = "trn_fx_day", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testOperation4Insert() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
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
	public void testOperation4UpdateWithMock() throws Exception {
		TrnFXDayRepository trnFXDayRepository = Mockito.mock(TrnFXDayRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXDayService fxDayService = new FXDayServiceImpl(trnFXDayRepository, messageService);
		/** 1. Test updated data not found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected TrnFXTick data.
		TrnFXDay day = new TrnFXDay();
		day.setTickKey(key);
		day.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(trnFXDayRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxDayService.operation(day, OperationMode.EDIT)).isInstanceOf(TradeException.class)
				.hasMessage("Error!");

		/** 2. Test updated data inconsistent */
		// Mockito expectations
		when(trnFXDayRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(day));
		// Expected TrnFXTick data.
		TrnFXDay newSixHour = new TrnFXDay();
		// Copy all data.
		BeanUtils.copyProperties(day, newSixHour);
		newSixHour.setUpdatedDate(ZonedDateTime.now().plusHours(1l));
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxDayService.operation(newSixHour, OperationMode.EDIT))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test update success */
		// Mockito expectations
		when(trnFXDayRepository.save(any(TrnFXDay.class))).thenReturn(day);
		// Execute the method being tested
		TrnFXDay fxDay = fxDayService.operation(day, OperationMode.EDIT);
		// Validation
		assertEquals(day, fxDay);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxday/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxday/operation/expectedData4Update.xml", table = "trn_fx_day", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testOperation4Update() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
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
	public void testOperation4DeleteWithMock() throws Exception {
		TrnFXDayRepository trnFXDayRepository = Mockito.mock(TrnFXDayRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXDayService fxDayService = new FXDayServiceImpl(trnFXDayRepository, messageService);
		/** 1. Test deleted data not found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected TrnFXTick data.
		TrnFXDay day = new TrnFXDay();
		day.setTickKey(key);
		day.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(trnFXDayRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxDayService.operation(day, OperationMode.DELETE)).isInstanceOf(TradeException.class)
				.hasMessage("Error!");

		/** 2. Test deleted data inconsistent */
		// Mockito expectations
		when(trnFXDayRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(day));
		// Expected TrnFXTick data.
		TrnFXDay newDay = new TrnFXDay();
		// Copy all data.
		BeanUtils.copyProperties(day, newDay);
		newDay.setUpdatedDate(ZonedDateTime.now().plusHours(1l));
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxDayService.operation(newDay, OperationMode.DELETE))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test deleted success */
		// Mockito expectations
		doNothing().when(trnFXDayRepository).delete(any(TrnFXDay.class));
		// Execute the method being tested
		TrnFXDay fxDay = fxDayService.operation(day, OperationMode.DELETE);
		// Validation
		assertNull(fxDay);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxday/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxday/operation/expectedData4Delete.xml", table = "trn_fx_day")
	public void testOperation4Delete() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		key.setRegistDate(dateTime);
		// New TrnFXTick data.
		Optional<TrnFXDay> fxDay = fxDayService.findOne(key);
		// Delete one from DB.
		fxDayService.operation(fxDay.get(), OperationMode.DELETE);
	}
}
