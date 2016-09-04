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
import com.akmans.trade.stock.service.ScaleService;
import com.akmans.trade.stock.springdata.jpa.entities.MstScale;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class ScaleServiceImplTest {

	@Autowired
	private ScaleService scaleService;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/scale/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve all data from DB.
		List<MstScale> scales = scaleService.findAll();
		assertEquals(13, scales.size());

		// Retrieve page data from DB.
		Page<MstScale> scales1 = scaleService.findAll(new PageRequest(0, 10));
		assertEquals(13, scales1.getTotalElements());
		assertEquals(10, scales1.getNumberOfElements());
		assertEquals(2, scales1.getTotalPages());
		// Retrieve page data from DB.
		Page<MstScale> scales2 = scaleService.findAll(new PageRequest(1, 10));
		assertEquals(13, scales2.getTotalElements());
		assertEquals(3, scales2.getNumberOfElements());
		assertEquals(2, scales2.getTotalPages());

		// Get one from DB by code = 1L.
		MstScale scale = scaleService.findOne(1);
		// Check result.
		assertEquals("Scale 1", scale.getName());
		assertEquals("user1", scale.getCreatedBy());
		assertNotNull(scale.getCreatedDate());
		assertEquals("user2", scale.getUpdatedBy());
		assertNotNull(scale.getUpdatedDate());
	}

	@Test
	@ExpectedDatabase(value = "/data/stock/service/scale/operation/expectedData4Insert.xml", table = "mst_scale", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		// New Scale data.
		MstScale scale = new MstScale();
		scale.setCode(2);
		scale.setName("Scale 2");
		// Do insert.
		scaleService.operation(scale, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/scale/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/stock/service/scale/operation/expectedData4Update.xml", table = "mst_scale", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		// Get one from DB by code = 10.
		MstScale scale = scaleService.findOne(10);
		// New Scale data.
		scale.setName("Scale Ten");
		// Do insert.
		scaleService.operation(scale, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/service/scale/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/stock/service/scale/operation/expectedData4Delete.xml", table = "mst_scale")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		// New Scale data.
		MstScale scale = scaleService.findOne(0);
		// Delete one from DB by code = 0.
		scaleService.operation(scale, OperationMode.DELETE);
	}
}
