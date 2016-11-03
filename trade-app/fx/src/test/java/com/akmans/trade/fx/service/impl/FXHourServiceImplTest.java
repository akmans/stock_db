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
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.service.FXTickBulkService;
import com.akmans.trade.fx.service.FXTickService;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXHourRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class FXHourServiceImplTest {

	private static final double DELTA = 1e-15;

	@Autowired
	private FXHourService fxHourService;

	@Test
	public void testFindOneWithMock() {
		FXTickService fxTickService = Mockito.mock(FXTickService.class);
		FXTickBulkService fxTickBulkService = Mockito.mock(FXTickBulkService.class);
		TrnFXHourRepository trnFXHourRepository = Mockito.mock(TrnFXHourRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXHourService fxHourService = new FXHourServiceImpl(fxTickService, fxTickBulkService, trnFXHourRepository, messageService);
		/** 1. Test found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		// Expected Hour data.
		TrnFXHour hour = new TrnFXHour();
		hour.setTickKey(key);
		Optional<TrnFXHour> option = Optional.of(hour);

		// Mockito expectations
		when(trnFXHourRepository.findOne(any(FXTickKey.class))).thenReturn(option);
		// Execute the method being tested
		Optional<TrnFXHour> fxHour = fxHourService.findOne(key);
		// Validation
		assertEquals(true, fxHour.isPresent());
		assertEquals(hour, fxHour.get());

		/** 2. Test not found */
		// Mockito expectations
		when(trnFXHourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		// Execute the method being tested
		fxHour = fxHourService.findOne(key);
		// Validation
		assertEquals(false, fxHour.isPresent());
	}

	@Test
	public void testFindPreviousWithMock() {
		FXTickService fxTickService = Mockito.mock(FXTickService.class);
		FXTickBulkService fxTickBulkService = Mockito.mock(FXTickBulkService.class);
		TrnFXHourRepository trnFXHourRepository = Mockito.mock(TrnFXHourRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXHourService fxHourService = new FXHourServiceImpl(fxTickService, fxTickBulkService, trnFXHourRepository, messageService);
		/** 1. Test when FXTickKey is null */
		// Execute the method being tested
		Optional<TrnFXHour> fxHour = fxHourService.findPrevious(null);
		// Validation
		assertEquals(false, fxHour.isPresent());

		/** 2. Test found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected Hour data.
		TrnFXHour hour = new TrnFXHour();
		hour.setTickKey(key);
		Optional<TrnFXHour> option = Optional.of(hour);

		// Mockito expectations
		when(trnFXHourRepository.findPrevious(any(String.class), any(LocalDateTime.class))).thenReturn(option);
		// Execute the method being tested
		fxHour = fxHourService.findPrevious(key);
		// Validation
		assertEquals(true, fxHour.isPresent());
		assertEquals(hour, fxHour.get());

		/** 3. Test not found */
		// Mockito expectations
		when(trnFXHourRepository.findPrevious(any(String.class), any(LocalDateTime.class)))
				.thenReturn(Optional.empty());
		// Execute the method being tested
		fxHour = fxHourService.findPrevious(key);
		// Validation
		assertEquals(false, fxHour.isPresent());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxhour/find/input.xml")
	public void testFind() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		key.setRegistDate(dateTime);
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
	public void testGenerateFXPeriodDataWithMock() throws Exception {
		FXTickService fxTickService = Mockito.mock(FXTickService.class);
		FXTickBulkService fxTickBulkService = Mockito.mock(FXTickBulkService.class);
		TrnFXHourRepository trnFXHourRepository = Mockito.mock(TrnFXHourRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXHourService fxHourService = new FXHourServiceImpl(fxTickService, fxTickBulkService, trnFXHourRepository, messageService);
		/** 1. Test FXType is HOUR */
		AbstractFXEntity entity = fxHourService.generateFXPeriodData(FXType.HOUR, "usdjpy", LocalDateTime.now());
		// Validation
		assertNull(entity);

		/** 2. Test FXType is SIXHOUR */
		List<TrnFXHour> hours = new ArrayList<TrnFXHour>();
		TrnFXHour hour0 = new TrnFXHour();
		hour0.setTickKey(new FXTickKey());
		hour0.setOpeningPrice(2);
		hour0.setHighPrice(4);
		hour0.setLowPrice(1);
		hour0.setFinishPrice(3);
		hours.add(hour0);
		TrnFXHour hour1 = new TrnFXHour();
		hour1.setTickKey(new FXTickKey());
		hour1.setOpeningPrice(20);
		hour1.setHighPrice(40);
		hour1.setLowPrice(10);
		hour1.setFinishPrice(30);
		hours.add(hour1);
		TrnFXHour hour2 = new TrnFXHour();
		hour2.setTickKey(new FXTickKey());
		hour2.setOpeningPrice(200);
		hour2.setHighPrice(400);
		hour2.setLowPrice(100);
		hour2.setFinishPrice(300);
		hours.add(hour2);
		// Mockito expectations
		when(trnFXHourRepository.findFXHourInPeriod(any(String.class), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(hours);
		entity = fxHourService.generateFXPeriodData(FXType.SIXHOUR, "usdjpy", LocalDateTime.now());
		// Validation
		assertNotNull(entity);
		assertEquals(true, entity instanceof TrnFX6Hour);
		assertEquals(2, entity.getOpeningPrice(), DELTA);
		assertEquals(400, entity.getHighPrice(), DELTA);
		assertEquals(1, entity.getLowPrice(), DELTA);
		assertEquals(300, entity.getFinishPrice(), DELTA);

		/** 3. Test FXType is DAY */
		entity = fxHourService.generateFXPeriodData(FXType.DAY, "usdjpy", LocalDateTime.now());
		// Validation
		assertNotNull(entity);
		assertEquals(true, entity instanceof TrnFXDay);
		assertEquals(2, entity.getOpeningPrice(), DELTA);
		assertEquals(400, entity.getHighPrice(), DELTA);
		assertEquals(1, entity.getLowPrice(), DELTA);
		assertEquals(300, entity.getFinishPrice(), DELTA);

		/** 4. Test FXType is WEEK */
		entity = fxHourService.generateFXPeriodData(FXType.WEEK, "usdjpy", LocalDateTime.now());
		// Validation
		assertNotNull(entity);
		assertEquals(true, entity instanceof TrnFXWeek);
		assertEquals(2, entity.getOpeningPrice(), DELTA);
		assertEquals(400, entity.getHighPrice(), DELTA);
		assertEquals(1, entity.getLowPrice(), DELTA);
		assertEquals(300, entity.getFinishPrice(), DELTA);

		/** 5. Test FXType is WEEK */
		entity = fxHourService.generateFXPeriodData(FXType.MONTH, "usdjpy", LocalDateTime.now());
		// Validation
		assertNotNull(entity);
		assertEquals(true, entity instanceof TrnFXMonth);
		assertEquals(2, entity.getOpeningPrice(), DELTA);
		assertEquals(400, entity.getHighPrice(), DELTA);
		assertEquals(1, entity.getLowPrice(), DELTA);
		assertEquals(300, entity.getFinishPrice(), DELTA);

		/** 6. Test FXHour data not found */
		when(trnFXHourRepository.findFXHourInPeriod(any(String.class), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(null);
		entity = fxHourService.generateFXPeriodData(FXType.WEEK, "usdjpy", LocalDateTime.now());
		// Validation
		assertNull(entity);
		when(trnFXHourRepository.findFXHourInPeriod(any(String.class), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(new ArrayList<TrnFXHour>());
		entity = fxHourService.generateFXPeriodData(FXType.WEEK, "usdjpy", LocalDateTime.now());
		// Validation
		assertNull(entity);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxhour/generate/input.xml")
	public void testGenerateFXPeriodData() throws Exception {
		// New DateTime
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		// Get one from DB.
		AbstractFXEntity hour = fxHourService.generateFXPeriodData(FXType.HOUR, "usdjpy", dateTime);
		// Check result.
		assertNull(hour);

		// Get one from DB.
		AbstractFXEntity sixHour = fxHourService.generateFXPeriodData(FXType.SIXHOUR, "usdjpy", dateTime);
		// Check result.
		assertEquals(true, sixHour instanceof TrnFX6Hour);
		assertEquals(10, sixHour.getOpeningPrice(), DELTA);
		assertEquals(23, sixHour.getHighPrice(), DELTA);
		assertEquals(99, sixHour.getLowPrice(), DELTA);
		assertEquals(4, sixHour.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity day = fxHourService.generateFXPeriodData(FXType.DAY, "usdjpy", dateTime);
		// Check result.
		assertEquals(true, day instanceof TrnFXDay);
		assertEquals(10, day.getOpeningPrice(), DELTA);
		assertEquals(25, day.getHighPrice(), DELTA);
		assertEquals(99, day.getLowPrice(), DELTA);
		assertEquals(6, day.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity week = fxHourService.generateFXPeriodData(FXType.WEEK, "usdjpy", dateTime);
		// Check result.
		assertEquals(true, week instanceof TrnFXWeek);
		assertEquals(10, week.getOpeningPrice(), DELTA);
		assertEquals(27, week.getHighPrice(), DELTA);
		assertEquals(99, week.getLowPrice(), DELTA);
		assertEquals(8, week.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity month = fxHourService.generateFXPeriodData(FXType.MONTH, "usdjpy", dateTime);
		// Check result.
		assertEquals(true, month instanceof TrnFXMonth);
		assertEquals(10, month.getOpeningPrice(), DELTA);
		assertEquals(29, month.getHighPrice(), DELTA);
		assertEquals(99, month.getLowPrice(), DELTA);
		assertEquals(10, month.getFinishPrice(), DELTA);
	}

	@Test
	public void testOperation4InsertWithMock() throws Exception {
		FXTickService fxTickService = Mockito.mock(FXTickService.class);
		FXTickBulkService fxTickBulkService = Mockito.mock(FXTickBulkService.class);
		TrnFXHourRepository trnFXHourRepository = Mockito.mock(TrnFXHourRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXHourService fxHourService = new FXHourServiceImpl(fxTickService, fxTickBulkService, trnFXHourRepository, messageService);
		/** 1. Test insert success */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		// Get TrnFXTick data from DB.
		TrnFXHour hour = new TrnFXHour();
		hour.setTickKey(key);

		// Mockito expectations
		when(trnFXHourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(trnFXHourRepository.save(any(TrnFXHour.class))).thenReturn(hour);
		// Execute the method being tested
		TrnFXHour fxHour = fxHourService.operation(hour, OperationMode.NEW);
		// Validation
		assertEquals(hour, fxHour);

		/** 2. Test key duplicated */
		// Mockito expectations
		when(trnFXHourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(hour));
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxHourService.operation(hour, OperationMode.NEW)).isInstanceOf(TradeException.class)
				.hasMessage("Error!");
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@ExpectedDatabase(value = "/data/fx/service/fxhour/operation/expectedData4Insert.xml", table = "trn_fx_hour", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testOperation4Insert() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		key.setRegistDate(dateTime);
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
	public void testOperation4UpdateWithMock() throws Exception {
		FXTickService fxTickService = Mockito.mock(FXTickService.class);
		FXTickBulkService fxTickBulkService = Mockito.mock(FXTickBulkService.class);
		TrnFXHourRepository trnFXHourRepository = Mockito.mock(TrnFXHourRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXHourService fxHourService = new FXHourServiceImpl(fxTickService, fxTickBulkService, trnFXHourRepository, messageService);
		/** 1. Test updated data not found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected TrnFXTick data.
		TrnFXHour hour = new TrnFXHour();
		hour.setTickKey(key);
		hour.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(trnFXHourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxHourService.operation(hour, OperationMode.EDIT)).isInstanceOf(TradeException.class)
				.hasMessage("Error!");

		/** 2. Test updated data inconsistent */
		// Mockito expectations
		when(trnFXHourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(hour));
		// Expected TrnFXTick data.
		TrnFXHour newSixHour = new TrnFXHour();
		// Copy all data.
		BeanUtils.copyProperties(hour, newSixHour);
		newSixHour.setUpdatedDate(ZonedDateTime.now().plusHours(1l));
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxHourService.operation(newSixHour, OperationMode.EDIT))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test update success */
		// Mockito expectations
		when(trnFXHourRepository.save(any(TrnFXHour.class))).thenReturn(hour);
		// Execute the method being tested
		TrnFXHour fxHour = fxHourService.operation(hour, OperationMode.EDIT);
		// Validation
		assertEquals(hour, fxHour);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxhour/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxhour/operation/expectedData4Update.xml", table = "trn_fx_hour", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testOperation4Update() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		key.setRegistDate(dateTime);
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
	public void testOperation4DeleteWithMock() throws Exception {
		FXTickService fxTickService = Mockito.mock(FXTickService.class);
		FXTickBulkService fxTickBulkService = Mockito.mock(FXTickBulkService.class);
		TrnFXHourRepository trnFXHourRepository = Mockito.mock(TrnFXHourRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXHourService fxHourService = new FXHourServiceImpl(fxTickService, fxTickBulkService, trnFXHourRepository, messageService);
		/** 1. Test deleted data not found */
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		key.setRegistDate(LocalDateTime.now());
		// Expected TrnFXTick data.
		TrnFXHour hour = new TrnFXHour();
		hour.setTickKey(key);
		hour.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(trnFXHourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxHourService.operation(hour, OperationMode.DELETE)).isInstanceOf(TradeException.class)
				.hasMessage("Error!");

		/** 2. Test deleted data inconsistent */
		// Mockito expectations
		when(trnFXHourRepository.findOne(any(FXTickKey.class))).thenReturn(Optional.of(hour));
		// Expected TrnFXTick data.
		TrnFXHour newHour = new TrnFXHour();
		// Copy all data.
		BeanUtils.copyProperties(hour, newHour);
		newHour.setUpdatedDate(ZonedDateTime.now().plusHours(1l));
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxHourService.operation(newHour, OperationMode.DELETE))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test deleted success */
		// Mockito expectations
		doNothing().when(trnFXHourRepository).delete(any(TrnFXHour.class));
		// Execute the method being tested
		TrnFXHour fxHour = fxHourService.operation(hour, OperationMode.DELETE);
		// Validation
		assertNull(fxHour);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxhour/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxhour/operation/expectedData4Delete.xml", table = "trn_fx_hour")
	public void testOperation4Delete() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		key.setRegistDate(dateTime);
		// New TrnFXTick data.
		Optional<TrnFXHour> fxHour = fxHourService.findOne(key);
		// Delete one from DB.
		fxHourService.operation(fxHour.get(), OperationMode.DELETE);
	}
}
