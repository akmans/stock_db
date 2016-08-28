package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;

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
