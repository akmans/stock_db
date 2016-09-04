package com.akmans.trade.cashing.service.impl;

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

import com.akmans.trade.cashing.service.SpecialItemService;
import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.core.enums.OperationMode;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class SpecialItemServiceImplTest {

	@Autowired
	private SpecialItemService specialItemService;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/cashing/service/specialitem/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve all data from DB.
		List<TrnSpecialItem> specialItems = specialItemService.findAll();
		assertEquals(14, specialItems.size());

		// Retrieve page data from DB.
		Page<TrnSpecialItem> specialItems1 = specialItemService.findAll(new PageRequest(0, 10));
		assertEquals(14, specialItems1.getTotalElements());
		assertEquals(10, specialItems1.getNumberOfElements());
		assertEquals(2, specialItems1.getTotalPages());
		// Retrieve page data from DB.
		Page<TrnSpecialItem> specialItems2 = specialItemService.findAll(new PageRequest(1, 10));
		assertEquals(14, specialItems2.getTotalElements());
		assertEquals(4, specialItems2.getNumberOfElements());
		assertEquals(2, specialItems2.getTotalPages());

		// Get one from DB by code = 1L.
		TrnSpecialItem specialItem = specialItemService.findOne(1);
		// Check result.
		assertEquals("jp1", specialItem.getName());
		assertEquals("user1", specialItem.getCreatedBy());
		assertNotNull(specialItem.getCreatedDate());
		assertEquals("user2", specialItem.getUpdatedBy());
		assertNotNull(specialItem.getUpdatedDate());
	}

	@Test
	@ExpectedDatabase(value = "/data/cashing/service/specialitem/operation/expectedData4Insert.xml", table = "trn_special_item", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		// New SpecialItem data.
		TrnSpecialItem specialItem = new TrnSpecialItem();
		specialItem.setName("cn2");
		// Do insert.
		specialItemService.operation(specialItem, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/cashing/service/specialItem/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/cashing/service/specialItem/operation/expectedData4Update.xml", table = "trn_special_item", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		// Get one from DB by code = 11.
		TrnSpecialItem specialItem = specialItemService.findOne(11);
		// New SpecialItem data.
		specialItem.setName("jp eleven");
		// Do insert.
		specialItemService.operation(specialItem, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/cashing/service/specialItem/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/cashing/service/specialItem/operation/expectedData4Delete.xml", table = "trn_special_item")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		// New SpecialItem data.
		TrnSpecialItem specialItem = specialItemService.findOne(10);
		// Delete one from DB by code = 10.
		specialItemService.operation(specialItem, OperationMode.DELETE);
	}
}
