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
public class TrnFXWeekRepositoryTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TrnFXWeekRepositoryTest.class);

	@Autowired
	private TrnFXWeekRepository fxWeekRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/repositories/fxweek/delete/input.xml")
	@ExpectedDatabase(value = "/data/fx/repositories/fxweek/delete/expectedData.xml", table = "trn_fx_week")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testDelete() throws Exception {
		// New FXWeekKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New Calendar data.
		Optional<TrnFXWeek> fxWeek = fxWeekRepository.findOne(key);
		logger.debug("The FXWeek is {}.", fxWeek.get());
		// New Calendar data.
		fxWeekRepository.delete(fxWeek.get());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/repositories/fxweek/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnFXWeek> fxWeeks1 = fxWeekRepository.findAll(new PageRequest(0, 10));
		assertEquals(15, fxWeeks1.getTotalElements());
		assertEquals(10, fxWeeks1.getNumberOfElements());
		assertEquals(2, fxWeeks1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnFXWeek> fxWeeks2 = fxWeekRepository.findAll(new PageRequest(1, 10));
		assertEquals(15, fxWeeks2.getTotalElements());
		assertEquals(5, fxWeeks2.getNumberOfElements());
		assertEquals(2, fxWeeks2.getTotalPages());

		// Retrieve all data from DB.
		List<TrnFXWeek> fxWeeks = fxWeekRepository.findAll();
		assertEquals(15, fxWeeks.size());

		// Count all data from DB.
		Long count = fxWeekRepository.count();
		assertEquals(15, count.longValue());

		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB by key.
		Optional<TrnFXWeek> fxWeek = fxWeekRepository.findOne(key);
		// Check result.
		assertEquals(true, fxWeek.isPresent());
		key.setCurrencyPair("nzdjpy");
		// Get one from DB by key.
		fxWeek = fxWeekRepository.findOne(key);
		// Check result.
		assertEquals(false, fxWeek.isPresent());
	}

	@Test
	@ExpectedDatabase(value = "/data/fx/repositories/fxweek/save/expectedData.xml", table = "trn_fx_week", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testSave() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New TrnFXWeek data.
		TrnFXWeek fxWeek = new TrnFXWeek();
		fxWeek.setTickKey(key);
		fxWeek.setOpeningPrice(2000);
		fxWeek.setHighPrice(4000);
		fxWeek.setLowPrice(1000);
		fxWeek.setFinishPrice(3000);
		fxWeek.setAvOpeningPrice(1500);
		fxWeek.setAvFinishPrice(2500);
		// Do save.
		fxWeek = fxWeekRepository.save(fxWeek);
		// Check result.
		assertNotNull(fxWeek.getCreatedBy());

		// Test update.
		fxWeek.setOpeningPrice(200);
		fxWeek.setHighPrice(400);
		fxWeek.setLowPrice(100);
		fxWeek.setFinishPrice(300);
		fxWeek.setAvOpeningPrice(150);
		fxWeek.setAvFinishPrice(250);
		// Do save.
		fxWeek = fxWeekRepository.save(fxWeek);
	}
}
