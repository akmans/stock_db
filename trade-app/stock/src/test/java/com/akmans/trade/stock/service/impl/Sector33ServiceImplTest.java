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
public class Sector33ServiceImplTest {

	@Autowired
	private Sector33Service sector33Service;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/sector33/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve all data from DB.
		List<MstSector33> sector33s = sector33Service.findAll();
		assertEquals(13, sector33s.size());

		// Retrieve page data from DB.
		Page<MstSector33> sector33s1 = sector33Service.findAll(new PageRequest(0, 10));
		assertEquals(13, sector33s1.getTotalElements());
		assertEquals(10, sector33s1.getNumberOfElements());
		assertEquals(2, sector33s1.getTotalPages());
		// Retrieve page data from DB.
		Page<MstSector33> sector33s2 = sector33Service.findAll(new PageRequest(1, 10));
		assertEquals(13, sector33s2.getTotalElements());
		assertEquals(3, sector33s2.getNumberOfElements());
		assertEquals(2, sector33s2.getTotalPages());

		// Get one from DB by code = 1L.
		MstSector33 sector33 = sector33Service.findOne(1);
		// Check result.
		assertEquals("Sector33 1", sector33.getName());
		assertEquals("user1", sector33.getCreatedBy());
		assertNotNull(sector33.getCreatedDate());
		assertEquals("user2", sector33.getUpdatedBy());
		assertNotNull(sector33.getUpdatedDate());
	}

	@Test
	@ExpectedDatabase(value = "/data/stock/service/sector33/operation/expectedData4Insert.xml", table = "mst_sector33", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		// New Sector33 data.
		MstSector33 sector33 = new MstSector33();
		sector33.setCode(2);
		sector33.setName("Sector33 2");
		// Do insert.
		sector33Service.operation(sector33, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/sector33/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/stock/service/sector33/operation/expectedData4Update.xml", table = "mst_sector33", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		// Get one from DB by code = 10.
		MstSector33 sector33 = sector33Service.findOne(10);
		// New Sector33 data.
		sector33.setName("Sector33 Ten");
		// Do insert.
		sector33Service.operation(sector33, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/sector33/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/stock/service/sector33/operation/expectedData4Delete.xml", table = "mst_sector33")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		// New Sector33 data.
		MstSector33 sector33 = sector33Service.findOne(0);
		// Delete one from DB by code = 0.
		sector33Service.operation(sector33, OperationMode.DELETE);
	}
}
