package com.akmans.trade.stock.service.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.stock.dto.InstrumentQueryDto;
import com.akmans.trade.stock.service.InstrumentService;
import com.akmans.trade.stock.service.MarketService;
import com.akmans.trade.stock.service.ScaleService;
import com.akmans.trade.stock.service.Sector17Service;
import com.akmans.trade.stock.service.Sector33Service;
import com.akmans.trade.stock.springdata.jpa.entities.MstInstrument;
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

		// Get one from DB by code = 1L.
		MstInstrument instrument = instrumentService.findOne(1000L);
		// Check result.
		assertEquals("The 1000 Instrument", instrument.getName());
		assertEquals("Y", instrument.getOnboard());
		assertEquals("user1", instrument.getCreatedBy());
		assertNotNull(instrument.getCreatedDate());
		assertEquals("user2", instrument.getUpdatedBy());
		assertNotNull(instrument.getUpdatedDate());

		// Get one eager from DB by code = 1L.
		MstInstrument instrument2 = instrumentService.findOneEager(1000L);
		// Check result.
		assertEquals("The 1000 Instrument", instrument.getName());
		assertEquals("Y", instrument2.getOnboard());
		assertEquals(10, instrument2.getMarket().getCode().intValue());
		assertEquals(1, instrument2.getScale().getCode().intValue());
		assertEquals(17, instrument2.getSector17().getCode().intValue());
		assertEquals(33, instrument2.getSector33().getCode().intValue());
		assertEquals("user1", instrument2.getCreatedBy());
		assertNotNull(instrument2.getCreatedDate());
		assertEquals("user2", instrument2.getUpdatedBy());
		assertNotNull(instrument2.getUpdatedDate());
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
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/instrument/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/stock/service/instrument/operation/expectedData4Update.xml", table = "mst_instrument", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		// Get one from DB by code = 10.
		MstInstrument instrument = instrumentService.findOne(1000L);
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
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/instrument/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/stock/service/instrument/operation/expectedData4Delete.xml", table = "mst_instrument")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		// New Instrument data.
		MstInstrument instrument = instrumentService.findOne(1001L);
		// Delete one from DB by code = 1001L.
		instrumentService.operation(instrument, OperationMode.DELETE);
	}
}
