package com.akmans.trade.cashing.springdata.jpa.repositories;

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

import com.akmans.trade.cashing.service.SpecialDetailService;
import com.akmans.trade.cashing.service.SpecialItemService;
import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialDetail;
import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.core.config.TestConfig;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class TrnSpecialDetailRepositoryTest {

	@Autowired
	private SpecialDetailService specialDetailService;

	@Autowired
	private SpecialItemService specialItemService;

	@Autowired
	private TrnSpecialDetailRepository specialDetailRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/cashing/repositories/specialdetail/delete/input.xml")
	@ExpectedDatabase(value = "/data/cashing/repositories/specialdetail/delete/expectedData.xml", table = "trn_special_detail")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testDelete() throws Exception {
		// New SpecialDetail data.
		TrnSpecialDetail specialDetail = specialDetailService.findOne(1L);
		// New SpecialDetail data.
		specialDetailRepository.delete(specialDetail);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/cashing/repositories/specialdetail/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnSpecialDetail> specialDetails1 = specialDetailRepository.findAll(new PageRequest(0, 10));
		assertEquals(14, specialDetails1.getTotalElements());
		assertEquals(10, specialDetails1.getNumberOfElements());
		assertEquals(2, specialDetails1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnSpecialDetail> specialDetails2 = specialDetailRepository.findAll(new PageRequest(1, 10));
		assertEquals(14, specialDetails2.getTotalElements());
		assertEquals(4, specialDetails2.getNumberOfElements());
		assertEquals(2, specialDetails2.getTotalPages());

		// Retrieve all data from DB.
		List<TrnSpecialDetail> specialDetails = specialDetailRepository.findAll();
		assertEquals(14, specialDetails.size());

		// Count all data from DB.
		Long count = specialDetailRepository.count();
		assertEquals(14, count.longValue());

		// TODO:code is auto generated ?!
		// Get one from DB by code.
		Optional<TrnSpecialDetail> specialDetail = specialDetailRepository.findOne(1L);
		// Check result.
		assertEquals(true, specialDetail.isPresent());
		// Get one from DB by code = 16L.
		specialDetail = specialDetailRepository.findOne(14L);
		// Check result.
		assertEquals(false, specialDetail.isPresent());

		// Get one from DB by code.
		List<Object[]> results = specialDetailRepository.findOneEager(1L);
		// Check result.
		assertEquals(1, results.size());
		assertEquals(2, results.get(0).length);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/cashing/repositories/specialdetail/save/input.xml")
	@ExpectedDatabase(value = "/data/cashing/repositories/specialdetail/save/expectedData.xml", table = "trn_special_detail", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testSave() throws Exception {
		// New SpecialDetail data.
		TrnSpecialDetail specialDetail = new TrnSpecialDetail();
		specialDetail.setName("jp");
		specialDetail.setDetail("1 detail");
		specialDetail.setAmount(10L);
		TrnSpecialItem item = specialItemService.findOne(100);
		specialDetail.setSpecialItem(item);
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 2, 0, 0, 0);
		specialDetail.setRegistDate(temp.getTime());
		// Do save.
		specialDetail = specialDetailRepository.save(specialDetail);
		// Check result.
		assertNotNull(specialDetail.getCreatedDate());

		// Test update.
		specialDetail.setName("cn");
		specialDetail.setDetail("0 detail");
		specialDetail.setAmount(11L);
		item = specialItemService.findOne(200);
		specialDetail.setSpecialItem(item);
		temp.set(2015, 0, 1, 0, 0, 0);
		specialDetail.setRegistDate(temp.getTime());
		// Do save.
		specialDetail = specialDetailRepository.save(specialDetail);
	}
}
