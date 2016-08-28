package com.akmans.trade.core.springdata.jpa.repositories;

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
import com.akmans.trade.core.service.CalendarService;
import com.akmans.trade.core.springdata.jpa.entities.MstCalendar;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class MstCalendarRepositoryTest {

	@Autowired
	private CalendarService calendarService;

	@Autowired
	private MstCalendarRepository calendarRepository;

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/repositories/calendar/delete/input.xml")
	@ExpectedDatabase(value = "/data/repositories/calendar/delete/expectedData.xml", table = "mst_calendar")
	public void testDelete() throws Exception {
		// New Calendar data.
		MstCalendar calendar = calendarService.findOne(0L);
		// New Calendar data.
		calendarRepository.delete(calendar);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/repositories/calendar/findallncountnfindone/input.xml")
	public void testFindAllnCountnFindOne() throws Exception {
		// Retrieve first page data from DB.
		Page<MstCalendar> calendars1 = calendarRepository.findAll(new PageRequest(0, 10));
		assertEquals(16, calendars1.getTotalElements());
		assertEquals(10, calendars1.getNumberOfElements());
		assertEquals(2, calendars1.getTotalPages());
		// Retrieve second page data from DB.
		Page<MstCalendar> calendars2 = calendarRepository.findAll(new PageRequest(1, 10));
		assertEquals(16, calendars2.getTotalElements());
		assertEquals(6, calendars2.getNumberOfElements());
		assertEquals(2, calendars2.getTotalPages());

		// Retrieve all data from DB.
		List<MstCalendar> calendars = calendarRepository.findAll();
		assertEquals(16, calendars.size());

		// Count all data from DB.
		Long count = calendarRepository.count();
		assertEquals(16, count.longValue());

		// Get one from DB by code = 1L.
		Optional<MstCalendar> calendar = calendarRepository.findOne(1L);
		// Check result.
		assertEquals(true, calendar.isPresent());
		// Get one from DB by code = 16L.
		calendar = calendarRepository.findOne(16L);
		// Check result.
		assertEquals(false, calendar.isPresent());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/repositories/calendar/empty.xml")
	@ExpectedDatabase(value = "/data/repositories/calendar/save/expectedData.xml", table = "mst_calendar", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testSave() throws Exception {
		// New Calendar data.
		MstCalendar calendar = new MstCalendar();
		calendar.setCalendar("cn");
		calendar.setHoliday(10);
		calendar.setDescription("Test CN Calendar");
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 11, 0, 0, 0);
		calendar.setRegistAt(temp.getTime());
		// Do save.
		calendar = calendarRepository.save(calendar);
		// Check result.
		assertNotNull(calendar.getCode());

		// Test update.
		calendar.setCalendar("jp");
		calendar.setHoliday(20);
		calendar.setDescription("Test JP Calendar");
		temp = java.util.Calendar.getInstance();
		temp.set(2016, 0, 11, 0, 0, 0);
		calendar.setRegistAt(temp.getTime());
		// Do save.
		calendar = calendarRepository.save(calendar);
	}
}
