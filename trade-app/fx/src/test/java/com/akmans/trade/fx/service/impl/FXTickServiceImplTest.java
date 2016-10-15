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
import com.akmans.trade.fx.service.FXTickService;
import com.akmans.trade.fx.springdata.jpa.entities.AbstractFXEntity;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.akmans.trade.fx.springdata.jpa.repositories.TrnFXTickRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class FXTickServiceImplTest {

	private static final double DELTA = 1e-15;

	@Autowired
	private FXTickService fxTickService;

	@Test
	public void testFindOneWithMock() throws Exception {
		TrnFXTickRepository trnFXTickRepository = Mockito.mock(TrnFXTickRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXTickService fxTickService = new FXTickServiceImpl(trnFXTickRepository, messageService);
		/** 1. Test found */
		// Expected Tick data.
		TrnFXTick tick = new TrnFXTick();
		tick.setCode(1L);
		Optional<TrnFXTick> option = Optional.of(tick);

		// Mockito expectations
		when(trnFXTickRepository.findOne(any(Long.class))).thenReturn(option);
		// Execute the method being tested
		Optional<TrnFXTick> fxTick = fxTickService.findOne(1L);
		// Validation
		assertEquals(tick, fxTick.get());

		/** 2. Test not found */
		// Mockito expectations
		when(trnFXTickRepository.findOne(any(Long.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested
		fxTick = fxTickService.findOne(1L);
		// Validation
		assertEquals(false, fxTick.isPresent());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxtick/find/input.xml")
	public void testFind() throws Exception {
		// New FXTickKey
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		// Get one from DB by key.
		TrnFXTick tick = fxTickService.findOne(1000L).get();
		// Check result.
		assertEquals("usdjpy", tick.getCurrencyPair());
		assertEquals(dateTime, tick.getRegistDate());
		assertEquals(100, tick.getBidPrice(), DELTA);
		assertEquals(200, tick.getAskPrice(), DELTA);
		assertEquals(150, tick.getMidPrice(), DELTA);
		assertEquals(0, tick.getProcessedFlag(), DELTA);
	}

	@Test
	public void testGenerateFXPeriodDataWithMock() throws Exception {
		TrnFXTickRepository trnFXTickRepository = Mockito.mock(TrnFXTickRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXTickService fxTickService = new FXTickServiceImpl(trnFXTickRepository, messageService);
		/** 1. Test FXType is HOUR */
		List<TrnFXTick> openingTicks = new ArrayList<TrnFXTick>();
		List<TrnFXTick> highestTicks = new ArrayList<TrnFXTick>();
		List<TrnFXTick> lowestTicks = new ArrayList<TrnFXTick>();
		List<TrnFXTick> finishTicks = new ArrayList<TrnFXTick>();
		TrnFXTick tick0 = new TrnFXTick();
		tick0.setAskPrice(1);
		tick0.setBidPrice(3);
		tick0.setMidPrice(2);
		openingTicks.add(tick0);
		TrnFXTick tick00 = new TrnFXTick();
		tick00.setAskPrice(1);
		tick00.setBidPrice(1);
		tick00.setMidPrice(1);
		lowestTicks.add(tick00);
		TrnFXTick tick1 = new TrnFXTick();
		tick1.setAskPrice(300);
		tick1.setBidPrice(500);
		tick1.setMidPrice(400);
		highestTicks.add(tick1);
		TrnFXTick tick2 = new TrnFXTick();
		tick2.setAskPrice(200);
		tick2.setBidPrice(400);
		tick2.setMidPrice(300);
		finishTicks.add(tick2);
		// Mockito expectations
//		when(trnFXTickRepository.findFXTickInPeriod(any(String.class), any(LocalDateTime.class),
//				any(LocalDateTime.class))).thenReturn(ticks);
		when(trnFXTickRepository.countFXTickInPeriod(any(String.class), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(1);
		when(trnFXTickRepository.findFirstFXTickInPeriod(any(String.class), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(openingTicks);
		when(trnFXTickRepository.findLastFXTickInPeriod(any(String.class), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(finishTicks);
		when(trnFXTickRepository.findHighestFXTickInPeriod(any(String.class), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(highestTicks);
		when(trnFXTickRepository.findLowestFXTickInPeriod(any(String.class), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(lowestTicks);
		AbstractFXEntity entity = fxTickService.generateFXPeriodData(FXType.HOUR, "usdjpy", LocalDateTime.now());
		// Validation
		assertNotNull(entity);
		assertEquals(true, entity instanceof TrnFXHour);
		assertEquals(2, entity.getOpeningPrice(), DELTA);
		assertEquals(400, entity.getHighPrice(), DELTA);
		assertEquals(1, entity.getLowPrice(), DELTA);
		assertEquals(300, entity.getFinishPrice(), DELTA);

		/** 2. Test FXType is SIXHOUR */
		entity = fxTickService.generateFXPeriodData(FXType.SIXHOUR, "usdjpy", LocalDateTime.now());
		// Validation
		assertNotNull(entity);
		assertEquals(true, entity instanceof TrnFX6Hour);
		assertEquals(2, entity.getOpeningPrice(), DELTA);
		assertEquals(400, entity.getHighPrice(), DELTA);
		assertEquals(1, entity.getLowPrice(), DELTA);
		assertEquals(300, entity.getFinishPrice(), DELTA);

		/** 3. Test FXType is DAY */
		entity = fxTickService.generateFXPeriodData(FXType.DAY, "usdjpy", LocalDateTime.now());
		// Validation
		assertNotNull(entity);
		assertEquals(true, entity instanceof TrnFXDay);
		assertEquals(2, entity.getOpeningPrice(), DELTA);
		assertEquals(400, entity.getHighPrice(), DELTA);
		assertEquals(1, entity.getLowPrice(), DELTA);
		assertEquals(300, entity.getFinishPrice(), DELTA);

		/** 4. Test FXType is WEEK */
		entity = fxTickService.generateFXPeriodData(FXType.WEEK, "usdjpy", LocalDateTime.now());
		// Validation
		assertNotNull(entity);
		assertEquals(true, entity instanceof TrnFXWeek);
		assertEquals(2, entity.getOpeningPrice(), DELTA);
		assertEquals(400, entity.getHighPrice(), DELTA);
		assertEquals(1, entity.getLowPrice(), DELTA);
		assertEquals(300, entity.getFinishPrice(), DELTA);

		/** 5. Test FXType is WEEK */
		entity = fxTickService.generateFXPeriodData(FXType.MONTH, "usdjpy", LocalDateTime.now());
		// Validation
		assertNotNull(entity);
		assertEquals(true, entity instanceof TrnFXMonth);
		assertEquals(2, entity.getOpeningPrice(), DELTA);
		assertEquals(400, entity.getHighPrice(), DELTA);
		assertEquals(1, entity.getLowPrice(), DELTA);
		assertEquals(300, entity.getFinishPrice(), DELTA);

		/** 6. Test FXTick data not found */
		when(trnFXTickRepository.countFXTickInPeriod(any(String.class), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(0);
		entity = fxTickService.generateFXPeriodData(FXType.WEEK, "usdjpy", LocalDateTime.now());
		// Validation
		assertNull(entity);
//		when(trnFXTickRepository.findFXTickInPeriod(any(String.class), any(LocalDateTime.class),
//				any(LocalDateTime.class))).thenReturn(new ArrayList<TrnFXTick>());
//		entity = fxTickService.generateFXPeriodData(FXType.WEEK, "usdjpy", LocalDateTime.now());
//		// Validation
//		assertNull(entity);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxtick/generate/input.xml")
	public void testGenerateFXPeriodData() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		key.setRegistDate(dateTime);
		// Get one from DB.
		AbstractFXEntity hour = fxTickService.generateFXPeriodData(FXType.HOUR, "usdjpy", dateTime);
		// Check result.
		assertEquals(true, hour instanceof TrnFXHour);
		assertEquals(100, hour.getOpeningPrice(), DELTA);
		assertEquals(112, hour.getHighPrice(), DELTA);
		assertEquals(99, hour.getLowPrice(), DELTA);
		assertEquals(111, hour.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity sixHour = fxTickService.generateFXPeriodData(FXType.SIXHOUR, "usdjpy", dateTime);
		// Check result.
		assertEquals(true, sixHour instanceof TrnFX6Hour);
		assertEquals(100, sixHour.getOpeningPrice(), DELTA);
		assertEquals(113, sixHour.getHighPrice(), DELTA);
		assertEquals(98, sixHour.getLowPrice(), DELTA);
		assertEquals(98, sixHour.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity day = fxTickService.generateFXPeriodData(FXType.DAY, "usdjpy", dateTime);
		// Check result.
		assertEquals(true, day instanceof TrnFXDay);
		assertEquals(100, day.getOpeningPrice(), DELTA);
		assertEquals(114, day.getHighPrice(), DELTA);
		assertEquals(97, day.getLowPrice(), DELTA);
		assertEquals(97, day.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity week = fxTickService.generateFXPeriodData(FXType.WEEK, "usdjpy", dateTime);
		// Check result.
		assertEquals(true, week instanceof TrnFXWeek);
		assertEquals(100, week.getOpeningPrice(), DELTA);
		assertEquals(115, week.getHighPrice(), DELTA);
		assertEquals(96, week.getLowPrice(), DELTA);
		assertEquals(96, week.getFinishPrice(), DELTA);

		// Get one from DB.
		AbstractFXEntity month = fxTickService.generateFXPeriodData(FXType.MONTH, "usdjpy", dateTime);
		// Check result.
		assertEquals(true, month instanceof TrnFXMonth);
		assertEquals(100, month.getOpeningPrice(), DELTA);
		assertEquals(116, month.getHighPrice(), DELTA);
		assertEquals(95, month.getLowPrice(), DELTA);
		assertEquals(95, month.getFinishPrice(), DELTA);
	}

	@Test
	public void testOperation4InsertWithMock() throws Exception {
		TrnFXTickRepository trnFXTickRepository = Mockito.mock(TrnFXTickRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXTickService fxTickService = new FXTickServiceImpl(trnFXTickRepository, messageService);
		/** 1. Test insert success */
		// Get TrnFXTick data from DB.
		TrnFXTick tick = new TrnFXTick();
		tick.setCode(1L);

		// Mockito expectations
		when(trnFXTickRepository.findOne(any(Long.class))).thenReturn(Optional.empty());
		when(trnFXTickRepository.save(any(TrnFXTick.class))).thenReturn(tick);
		// Execute the method being tested
		TrnFXTick fxTick = fxTickService.operation(tick, OperationMode.NEW);
		// Validation
		assertEquals(tick, fxTick);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@ExpectedDatabase(value = "/data/fx/service/fxtick/operation/expectedData4Insert.xml", table = "trn_fx_tick", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testOperation4Insert() throws Exception {
		// New DateTime
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		// Get TrnFXTick data from DB.
		TrnFXTick tick = new TrnFXTick();
		tick.setCurrencyPair("usdjpy");
		tick.setRegistDate(dateTime);
		tick.setBidPrice(10);
		tick.setAskPrice(20);
		tick.setMidPrice(100);
		tick.setProcessedFlag(0);
		// Do insert.
		fxTickService.operation(tick, OperationMode.NEW);
	}

	@Test
	public void testOperation4UpdateWithMock() throws Exception {
		TrnFXTickRepository trnFXTickRepository = Mockito.mock(TrnFXTickRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXTickService fxTickService = new FXTickServiceImpl(trnFXTickRepository, messageService);
		/** 1. Test updated data not found */
		// Expected TrnFXTick data.
		TrnFXTick tick = new TrnFXTick();
		tick.setCode(1L);
		tick.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(trnFXTickRepository.findOne(any(Long.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxTickService.operation(tick, OperationMode.EDIT)).isInstanceOf(TradeException.class)
				.hasMessage("Error!");

		/** 2. Test updated data inconsistent */
		// Mockito expectations
		when(trnFXTickRepository.findOne(any(Long.class))).thenReturn(Optional.of(tick));
		// Expected TrnFXTick data.
		TrnFXTick newSixTick = new TrnFXTick();
		// Copy all data.
		BeanUtils.copyProperties(tick, newSixTick);
		newSixTick.setUpdatedDate(ZonedDateTime.now().plusHours(1l));
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxTickService.operation(newSixTick, OperationMode.EDIT))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test update success */
		// Mockito expectations
		when(trnFXTickRepository.save(any(TrnFXTick.class))).thenReturn(tick);
		// Execute the method being tested
		TrnFXTick fxTick = fxTickService.operation(tick, OperationMode.EDIT);
		// Validation
		assertEquals(tick, fxTick);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxtick/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxtick/operation/expectedData4Update.xml", table = "trn_fx_tick", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testOperation4Update() throws Exception {
		// New DateTime
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:02:03.456", formatter);
		// New TrnFXTick data.
		TrnFXTick tick = fxTickService.findOne(1000L).get();
		tick.setCurrencyPair("usdjpy");
		tick.setRegistDate(dateTime);
		tick.setBidPrice(1);
		tick.setAskPrice(2);
		tick.setMidPrice(10);
		tick.setProcessedFlag(1);
		// Do insert.
		fxTickService.operation(tick, OperationMode.EDIT);
	}

	@Test
	public void testOperation4DeleteWithMock() throws Exception {
		TrnFXTickRepository trnFXTickRepository = Mockito.mock(TrnFXTickRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		FXTickService fxTickService = new FXTickServiceImpl(trnFXTickRepository, messageService);
		/** 1. Test deleted data not found */
		// Expected TrnFXTick data.
		TrnFXTick tick = new TrnFXTick();
		tick.setCode(1L);
		tick.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(trnFXTickRepository.findOne(any(Long.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxTickService.operation(tick, OperationMode.DELETE)).isInstanceOf(TradeException.class)
				.hasMessage("Error!");

		/** 2. Test deleted data inconsistent */
		// Mockito expectations
		when(trnFXTickRepository.findOne(any(Long.class))).thenReturn(Optional.of(tick));
		// Expected TrnFXTick data.
		TrnFXTick newTick = new TrnFXTick();
		// Copy all data.
		BeanUtils.copyProperties(tick, newTick);
		newTick.setUpdatedDate(ZonedDateTime.now().plusHours(1l));
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> fxTickService.operation(newTick, OperationMode.DELETE))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test deleted success */
		// Mockito expectations
		doNothing().when(trnFXTickRepository).delete(any(TrnFXTick.class));
		// Execute the method being tested
		TrnFXTick fxTick = fxTickService.operation(tick, OperationMode.DELETE);
		// Validation
		assertNull(fxTick);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/service/fxtick/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/fx/service/fxtick/operation/expectedData4Delete.xml", table = "trn_fx_tick")
	public void testOperation4Delete() throws Exception {
		// New TrnFXTick data.
		TrnFXTick tick = fxTickService.findOne(1000L).get();
		// Delete one from DB.
		fxTickService.operation(tick, OperationMode.DELETE);
	}
}
