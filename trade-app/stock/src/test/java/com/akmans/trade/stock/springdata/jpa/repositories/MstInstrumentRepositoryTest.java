package com.akmans.trade.stock.springdata.jpa.repositories;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;

import org.junit.After;
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
public class MstInstrumentRepositoryTest {

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

	@Autowired
	private MstInstrumentRepository instrumentRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/instrument/delete/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/instrument/delete/expectedData.xml", table = "mst_instrument")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testDelete() throws Exception {
		// New Instrument data.
		MstInstrument instrument = instrumentService.findOne(1001L);
		// New Instrument data.
		instrumentRepository.delete(instrument);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/instrument/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<MstInstrument> instruments1 = instrumentRepository.findAll(new PageRequest(0, 10));
		assertEquals(16, instruments1.getTotalElements());
		assertEquals(10, instruments1.getNumberOfElements());
		assertEquals(2, instruments1.getTotalPages());
		// Retrieve second page data from DB.
		Page<MstInstrument> instruments2 = instrumentRepository.findAll(new PageRequest(1, 10));
		assertEquals(16, instruments2.getTotalElements());
		assertEquals(6, instruments2.getNumberOfElements());
		assertEquals(2, instruments2.getTotalPages());

		// Retrieve all data from DB.
		List<MstInstrument> instruments = instrumentRepository.findAll();
		assertEquals(16, instruments.size());

		// Count all data from DB.
		Long count = instrumentRepository.count();
		assertEquals(16, count.longValue());

		// Get one from DB by code = 1000L.
		Optional<MstInstrument> instrument = instrumentRepository.findOne(1000L);
		// Check result.
		assertEquals(true, instrument.isPresent());
		// Get one from DB by code = 1016L.
		instrument = instrumentRepository.findOne(1016L);
		// Check result.
		assertEquals(false, instrument.isPresent());

		// Get one eager from DB by code = 1000L.
		List<Object[]> object = instrumentRepository.findOneEager(1000L);
		// Check result.
		assertNotNull(object);
		assertEquals(true, object.size() > 0);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/instrument/save/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/instrument/save/expectedData.xml", table = "mst_instrument", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testSave() throws Exception {
		// New Instrument data.
		MstInstrument instrument = new MstInstrument();
		instrument.setCode(1000L);
		instrument.setMarket(marketService.findOne(10));
		instrument.setScale(scaleService.findOne(1));
		instrument.setSector17(sector17Service.findOne(17));
		instrument.setSector33(sector33Service.findOne(33));
		instrument.setName("The 1001 Instrument");
		instrument.setOnboard("Y");
		// Do save.
		instrument = instrumentRepository.save(instrument);
		// Check result.
		assertNotNull(instrument.getCreatedDate());
		assertNotNull(instrument.getUpdatedDate());

		// Test update.
		instrument.setName("The 1000 Instrument");
		// Do save.
		instrument = instrumentRepository.save(instrument);
	}
}
