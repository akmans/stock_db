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
import com.akmans.trade.stock.service.Sector33Service;
import com.akmans.trade.stock.springdata.jpa.entities.MstSector33;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class MstSector33RepositoryTest {

	@Autowired
	private Sector33Service sector33Service;

	@Autowired
	private MstSector33Repository sector33Repository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/sector33/delete/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/sector33/delete/expectedData.xml", table = "mst_sector33")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testDelete() throws Exception {
		// New Sector33 data.
		MstSector33 sector33 = sector33Service.findOne(0);
		// New Sector33 data.
		sector33Repository.delete(sector33);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/sector33/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<MstSector33> sector33s1 = sector33Repository.findAll(new PageRequest(0, 10));
		assertEquals(13, sector33s1.getTotalElements());
		assertEquals(10, sector33s1.getNumberOfElements());
		assertEquals(2, sector33s1.getTotalPages());
		// Retrieve second page data from DB.
		Page<MstSector33> sector33s2 = sector33Repository.findAll(new PageRequest(1, 10));
		assertEquals(13, sector33s2.getTotalElements());
		assertEquals(3, sector33s2.getNumberOfElements());
		assertEquals(2, sector33s2.getTotalPages());

		// Retrieve all data from DB.
		List<MstSector33> sector33s = sector33Repository.findAll();
		assertEquals(13, sector33s.size());

		// Retrieve all data from DB with Order by code ascending.
		sector33s = sector33Repository.findAllByOrderByCodeAsc();
		assertEquals(0, sector33s.get(0).getCode().intValue());
		assertEquals(1, sector33s.get(1).getCode().intValue());
		assertEquals(12, sector33s.get(12).getCode().intValue());

		// Count all data from DB.
		Long count = sector33Repository.count();
		assertEquals(13, count.longValue());

		// Get one from DB by code = 1L.
		Optional<MstSector33> sector33 = sector33Repository.findOne(1);
		// Check result.
		assertEquals(true, sector33.isPresent());
		// Get one from DB by code = 13.
		sector33 = sector33Repository.findOne(13);
		// Check result.
		assertEquals(false, sector33.isPresent());
	}

	@Test
	@ExpectedDatabase(value = "/data/stock/repositories/sector33/save/expectedData.xml", table = "mst_sector33", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testSave() throws Exception {
		// New Sector33 data.
		MstSector33 sector33 = new MstSector33();
		sector33.setCode(21);
		sector33.setName("Sector33 2");
		// Do save.
		sector33 = sector33Repository.save(sector33);
		// Check result.
		assertNotNull(sector33.getCreatedDate());
		assertNotNull(sector33.getUpdatedDate());

		// Test update.
		sector33.setCode(21);
		sector33.setName("Sector33 21");
		// Do save.
		sector33 = sector33Repository.save(sector33);
	}
}
