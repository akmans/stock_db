package com.akmans.trade.cashing.service.impl;

import static org.junit.Assert.*;

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

import com.akmans.trade.cashing.dto.SpecialDetailQueryDto;
import com.akmans.trade.cashing.service.SpecialDetailService;
import com.akmans.trade.cashing.service.SpecialItemService;
import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialDetail;
import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.utils.DateUtil;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class SpecialDetailServiceImplTest {

	@Autowired
	private SpecialDetailService specialDetailService;

	@Autowired
	private SpecialItemService specialItemService;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/cashing/service/specialdetail/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testFind() throws Exception {
		// Create search criteria.
		SpecialDetailQueryDto criteria1 = new SpecialDetailQueryDto();
		criteria1.setName("jp");
		criteria1.setPageable(new PageRequest(0, 10));
		// Retrieve data from DB.
		Page<TrnSpecialDetail> calendars1 = specialDetailService.findPage(criteria1);
		assertEquals(7, calendars1.getTotalElements());
		assertEquals(7, calendars1.getNumberOfElements());
		assertEquals(1, calendars1.getTotalPages());
		// Create search criteria.
		SpecialDetailQueryDto criteria2 = new SpecialDetailQueryDto();
		criteria2.setItemCode(200);
		criteria2.setPageable(new PageRequest(0, 10));
		// Retrieve data from DB.
		Page<TrnSpecialDetail> calendars2 = specialDetailService.findPage(criteria2);
		assertEquals(6, calendars2.getTotalElements());
		assertEquals(6, calendars2.getNumberOfElements());
		assertEquals(1, calendars2.getTotalPages());

		// Retrieve page data from DB.
		Page<TrnSpecialDetail> specialDetails1 = specialDetailService.findAll(new PageRequest(0, 10));
		assertEquals(14, specialDetails1.getTotalElements());
		assertEquals(10, specialDetails1.getNumberOfElements());
		assertEquals(2, specialDetails1.getTotalPages());
		// Retrieve page data from DB.
		Page<TrnSpecialDetail> specialDetails2 = specialDetailService.findAll(new PageRequest(1, 10));
		assertEquals(14, specialDetails2.getTotalElements());
		assertEquals(4, specialDetails2.getNumberOfElements());
		assertEquals(2, specialDetails2.getTotalPages());

		// Get one from DB by code = 1L.
		TrnSpecialDetail specialDetail = specialDetailService.findOne(1L);
		// Check result.
		assertEquals("jp", specialDetail.getName());
		assertEquals("2015-01-01",
				DateUtil.formatDate(specialDetail.getRegistDate(), "yyyy-MM-dd"));
		assertEquals("1 detail", specialDetail.getDetail());
		assertEquals(12L, specialDetail.getAmount().longValue());
		assertEquals("user1", specialDetail.getCreatedBy());
		assertNotNull(specialDetail.getCreatedDate());
		assertEquals("user2", specialDetail.getUpdatedBy());
		assertNotNull(specialDetail.getUpdatedDate());
		// Get one eagerly from DB by code = 1L.
		specialDetail = specialDetailService.findOneEager(1L);
		// Check result.
		assertEquals(200, specialDetail.getSpecialItem().getCode().intValue());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/cashing/service/specialdetail/operation/input4Insert.xml")
	@ExpectedDatabase(value = "/data/cashing/service/specialdetail/operation/expectedData4Insert.xml", table = "trn_special_detail", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testOperation4Insert() throws Exception {
		// New SpecialDetail data.
		TrnSpecialDetail specialDetail = new TrnSpecialDetail();
		specialDetail.setName("cn");
		specialDetail.setDetail("0 detail");
		specialDetail.setAmount(11L);
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 1, 0, 0, 0);
		specialDetail.setRegistDate(temp.getTime());
		TrnSpecialItem item = specialItemService.findOne(100);
		specialDetail.setSpecialItem(item);
		// Do insert.
		specialDetailService.operation(specialDetail, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/cashing/service/specialDetail/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/cashing/service/specialDetail/operation/expectedData4Update.xml", table = "trn_special_detail", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testOperation4Update() throws Exception {
		// Get one from DB by code = 11.
		TrnSpecialDetail specialDetail = specialDetailService.findOne(1L);
		// New SpecialDetail data.
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 1, 0, 0, 0);
		specialDetail.setRegistDate(temp.getTime());
		specialDetail.setName("cn");
		specialDetail.setDetail("0 detail");
		specialDetail.setAmount(10L);
		TrnSpecialItem item = specialItemService.findOne(100);
		specialDetail.setSpecialItem(item);
		// Do update.
		specialDetailService.operation(specialDetail, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/cashing/service/specialdetail/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/cashing/service/specialdetail/operation/expectedData4Delete.xml", table = "trn_special_detail")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testOperation4Delete() throws Exception {
		// New SpecialDetail data.
		TrnSpecialDetail specialDetail = specialDetailService.findOne(1L);
		// Delete one from DB by key.
		specialDetailService.operation(specialDetail, OperationMode.DELETE);
	}
}
