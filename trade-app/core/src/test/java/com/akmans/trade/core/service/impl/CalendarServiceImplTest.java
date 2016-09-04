package com.akmans.trade.core.service.impl;

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

import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.core.dto.CalendarQueryDto;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.service.CalendarService;
import com.akmans.trade.core.springdata.jpa.entities.MstCalendar;
import com.akmans.trade.core.utils.DateUtil;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class CalendarServiceImplTest {

	@Autowired
	private CalendarService calendarService;

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/service/calendar/find/input.xml")
	public void testFindPage() throws Exception {
		// Create search criteria.
		CalendarQueryDto criteria1 = new CalendarQueryDto();
		criteria1.setCalendar("jp");
		criteria1.setPageable(new PageRequest(0, 10));
		// Retrieve data from DB.
		Page<MstCalendar> calendars1 = calendarService.findPage(criteria1);
		assertEquals(15, calendars1.getTotalElements());
		assertEquals(10, calendars1.getNumberOfElements());
		assertEquals(2, calendars1.getTotalPages());

		// Create search criteria.
		CalendarQueryDto criteria2 = new CalendarQueryDto();
		criteria2.setCalendar("jp");
		criteria2.setPageable(new PageRequest(1, 10));
		// Retrieve data from DB.
		Page<MstCalendar> calendars2 = calendarService.findPage(criteria2);
		assertEquals(15, calendars2.getTotalElements());
		assertEquals(5, calendars2.getNumberOfElements());
		assertEquals(2, calendars2.getTotalPages());

		// Create search criteria.
		CalendarQueryDto criteria3 = new CalendarQueryDto();
		criteria3.setCalendar("jp");
		criteria3.setHoliday(20);
		criteria3.setPageable(new PageRequest(0, 10));
		// Retrieve data from DB.
		Page<MstCalendar> calendars3 = calendarService.findPage(criteria3);
		assertEquals(1, calendars3.getTotalElements());
		assertEquals(1, calendars3.getNumberOfElements());
		assertEquals(1, calendars3.getTotalPages());
		// Check element.
		MstCalendar calendar = calendars3.getContent().get(0);
		assertEquals("jp", calendar.getCalendar());
		assertEquals(20, calendar.getHoliday().intValue());
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2016, 0, 11, 0, 0, 0);
		assertEquals(DateUtil.formatDate(temp.getTime(), "yyyy-MM-dd"),
				DateUtil.formatDate(calendar.getRegistAt(), "yyyy-MM-dd"));
		assertEquals("Test JP Calendar", calendar.getDescription());
		assertEquals("user1", calendar.getCreatedBy());
		assertNotNull(calendar.getCreatedDate());
		assertEquals("user2", calendar.getUpdatedBy());
		assertNotNull(calendar.getUpdatedDate());

		// Get one from DB by code = 1L.
		calendar = calendarService.findOne(1L);
		// Check result.
		assertEquals("jp", calendar.getCalendar());
		assertEquals(20, calendar.getHoliday().intValue());
		temp = java.util.Calendar.getInstance();
		temp.set(2016, 0, 11, 0, 0, 0);
		assertEquals(DateUtil.formatDate(temp.getTime(), "yyyy-MM-dd"),
				DateUtil.formatDate(calendar.getRegistAt(), "yyyy-MM-dd"));
		assertEquals("Test JP Calendar", calendar.getDescription());
		assertEquals("user1", calendar.getCreatedBy());
		assertNotNull(calendar.getCreatedDate());
		assertEquals("user2", calendar.getUpdatedBy());
		assertNotNull(calendar.getUpdatedDate());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/service/calendar/empty.xml")
	@ExpectedDatabase(value = "/data/service/calendar/operation/expectedData4Insert.xml", table = "mst_calendar", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testOperation4Insert() throws Exception {
		// New Calendar data.
		MstCalendar calendar = new MstCalendar();
		calendar.setCalendar("cn");
		calendar.setHoliday(10);
		calendar.setDescription("Test CN Calendar");
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 0, 11, 0, 0, 0);
		calendar.setRegistAt(temp.getTime());
		// Do insert.
		calendarService.operation(calendar, OperationMode.NEW);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/service/calendar/operation/input4Update.xml")
	@ExpectedDatabase(value = "/data/service/calendar/operation/expectedData4Update.xml", table = "mst_calendar", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testOperation4Update() throws Exception {
		// Get one from DB by code = 0L.
		MstCalendar calendar = calendarService.findOne(0L);
		// New Calendar data.
		calendar.setCalendar("jp");
		calendar.setHoliday(20);
		calendar.setDescription("Test CN Calendar Updated");
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2015, 10, 12, 0, 0, 0);
		calendar.setRegistAt(temp.getTime());
		// Do insert.
		calendarService.operation(calendar, OperationMode.EDIT);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/service/calendar/operation/input4Delete.xml")
	@ExpectedDatabase(value = "/data/service/calendar/operation/expectedData4Delete.xml", table = "mst_calendar")
	public void testOperation4Delete() throws Exception {
		// New Calendar data.
		MstCalendar calendar = calendarService.findOne(0L);
		// Delete one from DB by code = 0L.
		calendarService.operation(calendar, OperationMode.DELETE);
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/service/calendar/isjapanbusinessday/input.xml")
	public void testIsJapanBusinessDay() throws Exception {
		// Test 2016-01-09
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.set(2016, 0, 9, 0, 0, 0);
		assertEquals(false, calendarService.isJapanBusinessDay(temp.getTime()));
		// Test 2016-01-10
		temp = java.util.Calendar.getInstance();
		temp.set(2016, 0, 10, 0, 0, 0);
		assertEquals(false, calendarService.isJapanBusinessDay(temp.getTime()));
		// Test 2016-01-11
		temp = java.util.Calendar.getInstance();
		temp.set(2016, 0, 11, 0, 0, 0);
		assertEquals(false, calendarService.isJapanBusinessDay(temp.getTime()));
		// Test 2016-01-12
		temp = java.util.Calendar.getInstance();
		temp.set(2016, 0, 12, 0, 0, 0);
		assertEquals(true, calendarService.isJapanBusinessDay(temp.getTime()));
	}
}
