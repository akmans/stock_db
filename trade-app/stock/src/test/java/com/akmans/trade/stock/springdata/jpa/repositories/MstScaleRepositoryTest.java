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
public class MstScaleRepositoryTest {

	@Autowired
	private ScaleService scaleService;

	@Autowired
	private MstScaleRepository scaleRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/scale/delete/input.xml")
	@ExpectedDatabase(value = "/data/stock/repositories/scale/delete/expectedData.xml", table = "mst_scale")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testDelete() throws Exception {
		// New Scale data.
		MstScale scale = scaleService.findOne(0);
		// New Scale data.
		scaleRepository.delete(scale);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/stock/repositories/scale/find/input.xml")
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testFind() throws Exception {
		// Retrieve first page data from DB.
		Page<MstScale> scales1 = scaleRepository.findAll(new PageRequest(0, 10));
		assertEquals(13, scales1.getTotalElements());
		assertEquals(10, scales1.getNumberOfElements());
		assertEquals(2, scales1.getTotalPages());
		// Retrieve second page data from DB.
		Page<MstScale> scales2 = scaleRepository.findAll(new PageRequest(1, 10));
		assertEquals(13, scales2.getTotalElements());
		assertEquals(3, scales2.getNumberOfElements());
		assertEquals(2, scales2.getTotalPages());

		// Retrieve all data from DB.
		List<MstScale> scales = scaleRepository.findAll();
		assertEquals(13, scales.size());

		// Retrieve all data from DB with Order by code ascending.
		scales = scaleRepository.findAllByOrderByCodeAsc();
		assertEquals(0, scales.get(0).getCode().intValue());
		assertEquals(1, scales.get(1).getCode().intValue());
		assertEquals(12, scales.get(12).getCode().intValue());

		// Count all data from DB.
		Long count = scaleRepository.count();
		assertEquals(13, count.longValue());

		// Get one from DB by code = 1L.
		Optional<MstScale> scale = scaleRepository.findOne(1);
		// Check result.
		assertEquals(true, scale.isPresent());
		// Get one from DB by code = 13.
		scale = scaleRepository.findOne(13);
		// Check result.
		assertEquals(false, scale.isPresent());
	}

	@Test
	@ExpectedDatabase(value = "/data/stock/repositories/scale/save/expectedData.xml", table = "mst_scale", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/stock/emptyAll.xml")
	public void testSave() throws Exception {
		// New Scale data.
		MstScale scale = new MstScale();
		scale.setCode(21);
		scale.setName("Scale 2");
		// Do save.
		scale = scaleRepository.save(scale);
		// Check result.
		assertNotNull(scale.getCreatedDate());
		assertNotNull(scale.getUpdatedDate());

		// Test update.
		scale.setCode(21);
		scale.setName("Scale 21");
		// Do save.
		scale = scaleRepository.save(scale);
	}
}
