package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.akmans.trade.core.config.TestCoreConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestCoreConfig.class, loader = AnnotationConfigContextLoader.class)
public class CalendarTest {
	@Test
	public void testCalendar() {
		// Test value.
		assertEquals(Calendar.CHINA.toString(), "cn");
		assertEquals(Calendar.JAPAN.toString(), "jp");
		assertEquals(Calendar.CHINA.toString(), Calendar.CHINA.getValue());
		assertEquals(Calendar.JAPAN.toString(), Calendar.JAPAN.getValue());
		// Test Label.
		assertEquals(Calendar.CHINA.getLabel(), "China");
		assertEquals(Calendar.JAPAN.getLabel(), "Japan");
		// Test lookup(get).
		assertEquals(Calendar.get("cn"), Calendar.CHINA);
		assertEquals(Calendar.get("jp"), Calendar.JAPAN);
	}
}
