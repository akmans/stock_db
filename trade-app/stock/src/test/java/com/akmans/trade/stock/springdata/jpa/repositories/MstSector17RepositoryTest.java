package com.akmans.trade.stock.springdata.jpa.repositories;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;

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
import com.akmans.trade.stock.service.Sector17Service;
import com.akmans.trade.stock.springdata.jpa.entities.MstSector17;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class MstSector17RepositoryTest {

	@Autowired
	private Sector17Service sector17Service;

	@Autowired
	private MstSector17Repository sector17Repository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/sector17/delete/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/sector17/delete/expectedData.xml", table = "mst_sector17")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testDelete() throws Exception {
		// New Sector17 data.
		MstSector17 sector17 = sector17Service.findOne(0);
		// New Sector17 data.
		sector17Repository.delete(sector17);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/sector17/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<MstSector17> sector17s1 = sector17Repository.findAll(new PageRequest(0, 10));
		assertEquals(13, sector17s1.getTotalElements());
		assertEquals(10, sector17s1.getNumberOfElements());
		assertEquals(2, sector17s1.getTotalPages());
		// Retrieve second page data from DB.
		Page<MstSector17> sector17s2 = sector17Repository.findAll(new PageRequest(1, 10));
		assertEquals(13, sector17s2.getTotalElements());
		assertEquals(3, sector17s2.getNumberOfElements());
		assertEquals(2, sector17s2.getTotalPages());

		// Retrieve all data from DB.
		List<MstSector17> sector17s = sector17Repository.findAll();
		assertEquals(13, sector17s.size());

		// Retrieve all data from DB with Order by code ascending.
		sector17s = sector17Repository.findAllByOrderByCodeAsc();
		assertEquals(0, sector17s.get(0).getCode().intValue());
		assertEquals(1, sector17s.get(1).getCode().intValue());
		assertEquals(12, sector17s.get(12).getCode().intValue());

		// Count all data from DB.
		Long count = sector17Repository.count();
		assertEquals(13, count.longValue());

		// Get one from DB by code = 1L.
		Optional<MstSector17> sector17 = sector17Repository.findOne(1);
		// Check result.
		assertEquals(true, sector17.isPresent());
		// Get one from DB by code = 13.
		sector17 = sector17Repository.findOne(13);
		// Check result.
		assertEquals(false, sector17.isPresent());
	}

	@Test
	@ExpectedDatabase(value = "/data/stock/repositories/sector17/save/expectedData.xml", table = "mst_sector17", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testSave() throws Exception {
		// New Sector17 data.
		MstSector17 sector17 = new MstSector17();
		sector17.setCode(21);
		sector17.setName("Sector17 2");
		// Do save.
		sector17 = sector17Repository.save(sector17);
		// Check result.
		assertNotNull(sector17.getCreatedDate());
		assertNotNull(sector17.getUpdatedDate());

		// Test update.
		sector17.setCode(21);
		sector17.setName("Sector17 21");
		// Do save.
		sector17 = sector17Repository.save(sector17);
	}
}
