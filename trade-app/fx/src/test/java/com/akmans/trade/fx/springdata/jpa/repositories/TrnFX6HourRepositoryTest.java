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
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
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
public class TrnFX6HourRepositoryTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TrnFX6HourRepositoryTest.class);

	@Autowired
	private TrnFX6HourRepository fx6HourRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/repositories/fx6hour/delete/input.xml")
	@ExpectedDatabase(value = "/data/fx/repositories/fx6hour/delete/expectedData.xml", table = "trn_fx_6hour")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testDelete() throws Exception {
		// New FX6HourKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("usdjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New Calendar data.
		Optional<TrnFX6Hour> fx6Hour = fx6HourRepository.findOne(key);
		logger.debug("The FX6Hour is {}.", fx6Hour.get());
		// New Calendar data.
		fx6HourRepository.delete(fx6Hour.get());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/repositories/fx6hour/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnFX6Hour> fx6Hours1 = fx6HourRepository.findAll(new PageRequest(0, 10));
		assertEquals(15, fx6Hours1.getTotalElements());
		assertEquals(10, fx6Hours1.getNumberOfElements());
		assertEquals(2, fx6Hours1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnFX6Hour> fx6Hours2 = fx6HourRepository.findAll(new PageRequest(1, 10));
		assertEquals(15, fx6Hours2.getTotalElements());
		assertEquals(5, fx6Hours2.getNumberOfElements());
		assertEquals(2, fx6Hours2.getTotalPages());

		// Retrieve all data from DB.
		List<TrnFX6Hour> fx6Hours = fx6HourRepository.findAll();
		assertEquals(15, fx6Hours.size());

		// Count all data from DB.
		Long count = fx6HourRepository.count();
		assertEquals(15, count.longValue());

		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// Get one from DB by key.
		Optional<TrnFX6Hour> fx6Hour = fx6HourRepository.findOne(key);
		// Check result.
		assertEquals(true, fx6Hour.isPresent());
		key.setCurrencyPair("nzdjpy");
		// Get one from DB by key.
		fx6Hour = fx6HourRepository.findOne(key);
		// Check result.
		assertEquals(false, fx6Hour.isPresent());
	}

	@Test
	@ExpectedDatabase(value = "/data/fx/repositories/fx6hour/save/expectedData.xml", table = "trn_fx_6hour", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testSave() throws Exception {
		// New FXTickKey
		FXTickKey key = new FXTickKey();
		key.setCurrencyPair("audjpy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse("20160102 01:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The DateTime is {}.", result);
		key.setRegistDate(result);
		// New TrnFX6Hour data.
		TrnFX6Hour fx6Hour = new TrnFX6Hour();
		fx6Hour.setTickKey(key);
		fx6Hour.setOpeningPrice(2000);
		fx6Hour.setHighPrice(4000);
		fx6Hour.setLowPrice(1000);
		fx6Hour.setFinishPrice(3000);
		fx6Hour.setAvOpeningPrice(1500);
		fx6Hour.setAvFinishPrice(2500);
		// Do save.
		fx6Hour = fx6HourRepository.save(fx6Hour);
		// Check result.
		assertNotNull(fx6Hour.getCreatedBy());

		// Test update.
		fx6Hour.setOpeningPrice(200);
		fx6Hour.setHighPrice(400);
		fx6Hour.setLowPrice(100);
		fx6Hour.setFinishPrice(300);
		fx6Hour.setAvOpeningPrice(150);
		fx6Hour.setAvFinishPrice(250);
		// Do save.
		fx6Hour = fx6HourRepository.save(fx6Hour);
	}
}
