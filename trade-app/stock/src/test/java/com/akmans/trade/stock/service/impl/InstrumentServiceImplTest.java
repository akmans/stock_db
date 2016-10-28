package com.akmans.trade.stock.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.stock.dto.InstrumentQueryDto;
import com.akmans.trade.stock.service.InstrumentService;
import com.akmans.trade.stock.service.MarketService;
import com.akmans.trade.stock.service.ScaleService;
import com.akmans.trade.stock.service.Sector17Service;
import com.akmans.trade.stock.service.Sector33Service;
import com.akmans.trade.stock.springdata.jpa.entities.MstInstrument;
import com.akmans.trade.stock.springdata.jpa.repositories.MstInstrumentRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class InstrumentServiceImplTest {

	@Autowired
	private MarketService marketService;

	@Autowired
	private ScaleService scaleService;

	@Autowired
	private Sector17Service sector17Service;

	@Autowired
	private Sector33Service sector33Service;

	@Autowired
	private InstrumentService instrumentService;

	@Test
	public void testFindOneWithMock() throws Exception {
		MstInstrumentRepository mstInstrumentRepository = Mockito.mock(MstInstrumentRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		LocalContainerEntityManagerFactoryBean emf = Mockito.mock(LocalContainerEntityManagerFactoryBean.class);
		InstrumentService instrumentService = new InstrumentServiceImpl(mstInstrumentRepository, messageService, emf);
		/** 1. Test found */
		// Expected instrument data.
		MstInstrument instrument = new MstInstrument();
		instrument.setCode(1000l);
		instrument.setName("Test");
		Optional<MstInstrument> option = Optional.of(instrument);

		// Mockito expectations
		when(mstInstrumentRepository.findOne(any(Long.class))).thenReturn(option);
		// Execute the method being tested
		Optional<MstInstrument> expected = instrumentService.findOne(1000l);
		// Validation
		assertEquals(true, expected.isPresent());
		assertEquals(instrument, expected.get());

		/** 2. Test not found */
		// Mockito expectations
		when(mstInstrumentRepository.findOne(any(Long.class))).thenReturn(Optional.empty());
		// Execute the method being tested
		expected = instrumentService.findOne(1000l);
		// Validation
		assertEquals(false, expected.isPresent());
	}

	@Test
	public void testFindAllWithMock() throws Exception {
		MstInstrumentRepository mstInstrumentRepository = Mockito.mock(MstInstrumentRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		LocalContainerEntityManagerFactoryBean emf = Mockito.mock(LocalContainerEntityManagerFactoryBean.class);
		InstrumentService instrumentService = new InstrumentServiceImpl(mstInstrumentRepository, messageService, emf);
		/** 1. Test found */
		// Expected instrument data.
		MstInstrument instrument = new MstInstrument();
		instrument.setCode(1000l);
		instrument.setName("Test");
		List<MstInstrument> expected = new ArrayList<MstInstrument>();
		expected.add(instrument);

		// Mockito expectations
		when(mstInstrumentRepository.findAll()).thenReturn(expected);
		// Execute the method being tested
		List<MstInstrument> actual = instrumentService.findAll();
		// Validation
		assertEquals(expected, actual);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/instrument/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve all data from DB.
		List<MstInstrument> instruments = instrumentService.findAll();
		assertEquals(16, instruments.size());

		// Create search criteria.
		InstrumentQueryDto criteria1 = new InstrumentQueryDto();
		criteria1.setPageable(new PageRequest(0, 10));
		// Retrieve page data from DB.
		Page<MstInstrument> instruments1 = instrumentService.findPage(criteria1);
		assertEquals(16, instruments1.getTotalElements());
		assertEquals(10, instruments1.getNumberOfElements());
		assertEquals(2, instruments1.getTotalPages());
		// Create search criteria.
		InstrumentQueryDto criteria2 = new InstrumentQueryDto();
		criteria2.setPageable(new PageRequest(1, 10));
		// Retrieve page data from DB.
		Page<MstInstrument> instruments2 = instrumentService.findPage(criteria2);
		assertEquals(16, instruments2.getTotalElements());
		assertEquals(6, instruments2.getNumberOfElements());
		assertEquals(2, instruments2.getTotalPages());
		// Create search criteria.
		InstrumentQueryDto criteria3 = new InstrumentQueryDto();
		criteria3.setCode(1000L);
		criteria3.setPageable(new PageRequest(0, 10));
		// Retrieve page data from DB.
		Page<MstInstrument> instruments3 = instrumentService.findPage(criteria3);
		assertEquals(1, instruments3.getTotalElements());
		// Create search criteria.
		InstrumentQueryDto criteria4 = new InstrumentQueryDto();
		criteria4.setMarket(10);
		criteria4.setScale(1);
		criteria4.setSector17(17);
		criteria4.setSector33(33);
		criteria4.setOnboard("Y");
		criteria4.setPageable(new PageRequest(0, 10));
		// Retrieve page data from DB.
		Page<MstInstrument> instruments4 = instrumentService.findPage(criteria4);
		assertEquals(8, instruments4.getTotalElements());

		// Get one from DB by code.
		MstInstrument instrument = instrumentService.findOne(1000L).get();
		// Check result.
		assertEquals("The 1000 Instrument", instrument.getName());
		assertEquals("Y", instrument.getOnboard());
		assertEquals("user1", instrument.getCreatedBy());
		assertNotNull(instrument.getCreatedDate());
		assertEquals("user2", instrument.getUpdatedBy());
		assertNotNull(instrument.getUpdatedDate());
		assertEquals("The 1000 Instrument", instrument.getName());
		assertEquals("Y", instrument.getOnboard());
		assertEquals(10, instrument.getMarket().getCode().intValue());
		assertEquals(1, instrument.getScale().getCode().intValue());
		assertEquals(17, instrument.getSector17().getCode().intValue());
		assertEquals(33, instrument.getSector33().getCode().intValue());
		assertEquals("user1", instrument.getCreatedBy());
		assertNotNull(instrument.getCreatedDate());
		assertEquals("user2", instrument.getUpdatedBy());
		assertNotNull(instrument.getUpdatedDate());
	}

	@Test
	public void testOperation4InsertWithMock() throws Exception {
		MstInstrumentRepository mstInstrumentRepository = Mockito.mock(MstInstrumentRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		LocalContainerEntityManagerFactoryBean emf = Mockito.mock(LocalContainerEntityManagerFactoryBean.class);
		InstrumentService instrumentService = new InstrumentServiceImpl(mstInstrumentRepository, messageService, emf);
		/** 1. Test insert success */
		// New MstInstrument
		MstInstrument expected = new MstInstrument();
		expected.setCode(1000l);
		expected.setName("Test");

		// Mockito expectations
		when(mstInstrumentRepository.findOne(any(Long.class))).thenReturn(Optional.empty());
		when(mstInstrumentRepository.save(any(MstInstrument.class))).thenReturn(expected);
		// Execute the method being tested
		MstInstrument actual = instrumentService.operation(expected, OperationMode.NEW);
		// Validation
		assertEquals(expected, actual);

		/** 2. Test key duplicated */
		// Mockito expectations
		when(mstInstrumentRepository.findOne(any(Long.class))).thenReturn(Optional.of(expected));
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> instrumentService.operation(expected, OperationMode.NEW))
				.isInstanceOf(TradeException.class).hasMessage("Error!");
	}


	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/instrument/operation/input4Insert.xml")
	@ExpectedDatabase(value = "/data/stock/service/instrument/operation/expectedData4Insert.xml", table = "mst_instrument", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		// New Instrument data.
		MstInstrument instrument = new MstInstrument();
		instrument.setCode(1000L);
		instrument.setMarket(marketService.findOne(10));
		instrument.setScale(scaleService.findOne(1));
		instrument.setSector17(sector17Service.findOne(17));
		instrument.setSector33(sector33Service.findOne(33));
		instrument.setName("The 1000 Instrument");
		instrument.setOnboard("Y");
		// Do insert.
		instrumentService.operation(instrument, OperationMode.NEW);
	}

	@Test
	public void testOperation4UpdateWithMock() throws Exception {
		MstInstrumentRepository mstInstrumentRepository = Mockito.mock(MstInstrumentRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		LocalContainerEntityManagerFactoryBean emf = Mockito.mock(LocalContainerEntityManagerFactoryBean.class);
		InstrumentService instrumentService = new InstrumentServiceImpl(mstInstrumentRepository, messageService, emf);
		/** 1. Test updated data not found */
		// New MstInstrument
		MstInstrument expected = new MstInstrument();
		expected.setCode(1000l);
		expected.setName("Test");
		expected.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(mstInstrumentRepository.findOne(any(Long.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> instrumentService.operation(expected, OperationMode.EDIT))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 2. Test updated data inconsistent */
		// Mockito expectations
		when(mstInstrumentRepository.findOne(any(Long.class))).thenReturn(Optional.of(expected));
		// Expected MstInstrument data.
		MstInstrument newExpected = new MstInstrument();
		// Copy all data.
		BeanUtils.copyProperties(expected, newExpected);
		newExpected.setUpdatedDate(ZonedDateTime.now().plusHours(1l));
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> instrumentService.operation(newExpected, OperationMode.EDIT))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test update success */
		// Mockito expectations
		when(mstInstrumentRepository.save(any(MstInstrument.class))).thenReturn(expected);
		// Execute the method being tested
		MstInstrument actual = instrumentService.operation(expected, OperationMode.EDIT);
		// Validation
		assertEquals(expected, actual);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/instrument/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/stock/service/instrument/operation/expectedData4Update.xml", table = "mst_instrument", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		// Get one from DB by code = 10.
		MstInstrument instrument = instrumentService.findOne(1000L).get();
		instrument.setMarket(marketService.findOne(11));
		instrument.setScale(scaleService.findOne(2));
		instrument.setSector17(sector17Service.findOne(18));
		instrument.setSector33(sector33Service.findOne(34));
		instrument.setName("The 1001 Instrument");
		instrument.setOnboard("N");
		// Do insert.
		instrumentService.operation(instrument, OperationMode.EDIT);
	}

	@Test
	public void testOperation4DeleteWithMock() throws Exception {
		MstInstrumentRepository mstInstrumentRepository = Mockito.mock(MstInstrumentRepository.class);
		MessageService messageService = Mockito.mock(MessageService.class);
		LocalContainerEntityManagerFactoryBean emf = Mockito.mock(LocalContainerEntityManagerFactoryBean.class);
		InstrumentService instrumentService = new InstrumentServiceImpl(mstInstrumentRepository, messageService, emf);
		/** 1. Test updated data not found */
		// New MstInstrument
		MstInstrument expected = new MstInstrument();
		expected.setCode(1000l);
		expected.setName("Test");
		expected.setUpdatedDate(ZonedDateTime.now());

		// Mockito expectations
		when(mstInstrumentRepository.findOne(any(Long.class))).thenReturn(Optional.empty());
		when(messageService.getMessage(any(String.class), any(Object.class))).thenReturn("Error!");
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> instrumentService.operation(expected, OperationMode.DELETE))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 2. Test deleted data inconsistent */
		// Mockito expectations
		when(mstInstrumentRepository.findOne(any(Long.class))).thenReturn(Optional.of(expected));
		// Expected TrnFXTick data.
		MstInstrument newInstrument = new MstInstrument();
		// Copy all data.
		BeanUtils.copyProperties(expected, newInstrument);
		newInstrument.setUpdatedDate(ZonedDateTime.now().plusHours(1l));
		// Execute the method being tested with validation.
		assertThatThrownBy(() -> instrumentService.operation(newInstrument, OperationMode.DELETE))
				.isInstanceOf(TradeException.class).hasMessage("Error!");

		/** 3. Test deleted success */
		// Mockito expectations
		doNothing().when(mstInstrumentRepository).delete(any(MstInstrument.class));
		// Execute the method being tested
		MstInstrument result = instrumentService.operation(expected, OperationMode.DELETE);
		// Validation
		assertNull(result);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/instrument/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/stock/service/instrument/operation/expectedData4Delete.xml", table = "mst_instrument")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		// New Instrument data.
		MstInstrument instrument = instrumentService.findOne(1001L).get();
		// Delete one from DB by code = 1001L.
		instrumentService.operation(instrument, OperationMode.DELETE);
	}
}
