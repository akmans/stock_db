package com.akmans.trade.fx.springdata.jpa.repositories;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
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
public class TrnFXMonthRepositoryTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TrnFXMonthRepositoryTest.class);

	private static final double DELTA = 1e-15;

	@Autowired
	private TrnFXMonthRepository fxMonthRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/repositories/fxmonth/delete/input.xml")
	@ExpectedDatabase(value = "/data/fx/repositories/fxmonth/delete/expectedData.xml", table = "trn_fx_month")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testDelete() throws Exception {
		// New FXMonthKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160101 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New Calendar data.
		Optional<TrnFXMonth> fxMonth = fxMonthRepository.findOne(key);
		logger.debug("The FXMonth is {}.", fxMonth.get());
		// New Calendar data.
		fxMonthRepository.delete(fxMonth.get());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/repositories/fxmonth/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnFXMonth> fxMonths1 = fxMonthRepository.findAll(new PageRequest(0, 10));
		assertEquals(15, fxMonths1.getTotalElements());
		assertEquals(10, fxMonths1.getNumberOfElements());
		assertEquals(2, fxMonths1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnFXMonth> fxMonths2 = fxMonthRepository.findAll(new PageRequest(1, 10));
		assertEquals(15, fxMonths2.getTotalElements());
		assertEquals(5, fxMonths2.getNumberOfElements());
		assertEquals(2, fxMonths2.getTotalPages());

		// Retrieve all data from DB.
		List<TrnFXMonth> fxMonths = fxMonthRepository.findAll();
		assertEquals(15, fxMonths.size());

		// Count all data from DB.
		Long count = fxMonthRepository.count();
		assertEquals(15, count.longValue());

		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160101 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB by key.
		Optional<TrnFXMonth> fxMonth = fxMonthRepository.findOne(key);
		// Check result.
		assertEquals(true, fxMonth.isPresent());
		key.setCurrencyPair("nzdjpy");
		// Get one from DB by key.
		fxMonth = fxMonthRepository.findOne(key);
		// Check result.
		assertEquals(false, fxMonth.isPresent());

		// Test findPrevious method.
		Optional<TrnFXMonth> fxPreviousMonth = fxMonthRepository.findPrevious("usdjpy", result);
		// Check result.
		assertEquals(false, fxPreviousMonth.isPresent());
		// Next hour.
		fxPreviousMonth = fxMonthRepository.findPrevious("usdjpy", result.plusMonths(1));
		// Check result.
		assertEquals(true, fxPreviousMonth.isPresent());
		assertEquals("usdjpy", fxPreviousMonth.get().getTickKey().getCurrencyPair());
		assertEquals(2, fxPreviousMonth.get().getOpeningPrice(), DELTA);
		assertEquals(4, fxPreviousMonth.get().getHighPrice(), DELTA);
		assertEquals(1, fxPreviousMonth.get().getLowPrice(), DELTA);
		assertEquals(3, fxPreviousMonth.get().getFinishPrice(), DELTA);
		assertEquals(1.5, fxPreviousMonth.get().getAvOpeningPrice(), DELTA);
		assertEquals(2.5, fxPreviousMonth.get().getAvFinishPrice(), DELTA);
		// Another next hour.
		fxPreviousMonth = fxMonthRepository.findPrevious("usdjpy", result.plusMonths(2));
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
	@ExpectedDatabase(value = "/data/fx/repositories/fxmonth/save/expectedData.xml", table = "trn_fx_month", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testSave() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160101 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New TrnFXMonth data.
		TrnFXMonth fxMonth = new TrnFXMonth();
		fxMonth.setTickKey(key);
		fxMonth.setOpeningPrice(2000);
		fxMonth.setHighPrice(4000);
		fxMonth.setLowPrice(1000);
		fxMonth.setFinishPrice(3000);
		fxMonth.setAvOpeningPrice(1500);
		fxMonth.setAvFinishPrice(2500);
		// Do save.
		fxMonth = fxMonthRepository.save(fxMonth);
		// Check result.
		assertNotNull(fxMonth.getCreatedBy());

		// Test update.
		fxMonth.setOpeningPrice(200);
		fxMonth.setHighPrice(400);
		fxMonth.setLowPrice(100);
		fxMonth.setFinishPrice(300);
		fxMonth.setAvOpeningPrice(150);
		fxMonth.setAvFinishPrice(250);
		// Do save.
		fxMonth = fxMonthRepository.save(fxMonth);
	}
}
