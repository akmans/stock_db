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
public class Sector17ServiceImplTest {

	@Autowired
	private Sector17Service sector17Service;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/sector17/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve all data from DB.
		List<MstSector17> sector17s = sector17Service.findAll();
		assertEquals(13, sector17s.size());

		// Retrieve page data from DB.
		Page<MstSector17> sector17s1 = sector17Service.findAll(new PageRequest(0, 10));
		assertEquals(13, sector17s1.getTotalElements());
		assertEquals(10, sector17s1.getNumberOfElements());
		assertEquals(2, sector17s1.getTotalPages());
		// Retrieve page data from DB.
		Page<MstSector17> sector17s2 = sector17Service.findAll(new PageRequest(1, 10));
		assertEquals(13, sector17s2.getTotalElements());
		assertEquals(3, sector17s2.getNumberOfElements());
		assertEquals(2, sector17s2.getTotalPages());

		// Get one from DB by code = 1L.
		MstSector17 sector17 = sector17Service.findOne(1);
		// Check result.
		assertEquals("Sector17 1", sector17.getName());
		assertEquals("user1", sector17.getCreatedBy());
		assertNotNull(sector17.getCreatedDate());
		assertEquals("user2", sector17.getUpdatedBy());
		assertNotNull(sector17.getUpdatedDate());
	}

	@Test
	@ExpectedDatabase(value = "/data/stock/service/sector17/operation/expectedData4Insert.xml", table = "mst_sector17", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		// New Sector17 data.
		MstSector17 sector17 = new MstSector17();
		sector17.setCode(2);
		sector17.setName("Sector17 2");
		// Do insert.
		sector17Service.operation(sector17, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/sector17/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/stock/service/sector17/operation/expectedData4Update.xml", table = "mst_sector17", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		// Get one from DB by code = 10.
		MstSector17 sector17 = sector17Service.findOne(10);
		// New Sector17 data.
		sector17.setName("Sector17 Ten");
		// Do insert.
		sector17Service.operation(sector17, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/sector17/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/stock/service/sector17/operation/expectedData4Delete.xml", table = "mst_sector17")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		// New Sector17 data.
		MstSector17 sector17 = sector17Service.findOne(0);
		// Delete one from DB by code = 0.
		sector17Service.operation(sector17, OperationMode.DELETE);
	}
}
