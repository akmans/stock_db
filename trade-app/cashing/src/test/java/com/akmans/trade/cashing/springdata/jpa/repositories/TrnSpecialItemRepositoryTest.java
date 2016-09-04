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

import com.akmans.trade.cashing.service.SpecialItemService;
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
public class TrnSpecialItemRepositoryTest {

	@Autowired
	private SpecialItemService specialItemService;

	@Autowired
	private TrnSpecialItemRepository specialItemRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/cashing/repositories/specialitem/delete/input.xml")
	@ExpectedDatabase(value = "/data/cashing/repositories/specialitem/delete/expectedData.xml", table = "trn_special_item")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testDelete() throws Exception {
		// New SpecialItem data.
		TrnSpecialItem specialItem = specialItemService.findOne(1);
		// New SpecialItem data.
		specialItemRepository.delete(specialItem);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/cashing/repositories/specialItem/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testFindAllnCountnFindOne() throws Exception {
		// Retrieve first page data from DB.
		Page<TrnSpecialItem> specialItems1 = specialItemRepository.findAll(new PageRequest(0, 10));
		assertEquals(14, specialItems1.getTotalElements());
		assertEquals(10, specialItems1.getNumberOfElements());
		assertEquals(2, specialItems1.getTotalPages());
		// Retrieve second page data from DB.
		Page<TrnSpecialItem> specialItems2 = specialItemRepository.findAll(new PageRequest(1, 10));
		assertEquals(14, specialItems2.getTotalElements());
		assertEquals(4, specialItems2.getNumberOfElements());
		assertEquals(2, specialItems2.getTotalPages());

		// Retrieve all data from DB.
		List<TrnSpecialItem> specialItems = specialItemRepository.findAll();
		assertEquals(14, specialItems.size());

		// Retrieve all data from DB with ordering.
		specialItems = specialItemRepository.findAllByOrderByCodeAsc();
		assertEquals(0, specialItems.get(0).getCode().intValue());
		assertEquals(13, specialItems.get(13).getCode().intValue());

		// Count all data from DB.
		Long count = specialItemRepository.count();
		assertEquals(14, count.longValue());

		// Get one from DB by code.
		Optional<TrnSpecialItem> specialItem = specialItemRepository.findOne(1);
		// Check result.
		assertEquals(true, specialItem.isPresent());
		// Get one from DB by code = 16L.
		specialItem = specialItemRepository.findOne(14);
		// Check result.
		assertEquals(false, specialItem.isPresent());
	}

	@Test
	@ExpectedDatabase(value = "/data/cashing/repositories/specialitem/save/expectedData.xml", table = "trn_special_item", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/cashing/emptyAll.xml")
	public void testSave() throws Exception {
		// New SpecialItem data.
		TrnSpecialItem specialItem = new TrnSpecialItem();
		specialItem.setName("jp");
		// Do save.
		specialItem = specialItemRepository.save(specialItem);
		// Check result.
		assertNotNull(specialItem.getCreatedDate());

		// Test update.
		specialItem.setName("cn");
		// Do save.
		specialItem = specialItemRepository.save(specialItem);
	}
}
