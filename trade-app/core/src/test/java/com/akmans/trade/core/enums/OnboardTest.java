package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class OnboardTest {
	@Test
	public void testOnboard() {
		// Test value.
		assertEquals(Onboard.ON.toString(), "Y");
		assertEquals(Onboard.OFF.toString(), "N");
		assertEquals(Onboard.ON.toString(), Onboard.ON.getValue());
		assertEquals(Onboard.OFF.toString(), Onboard.OFF.getValue());
		// Test Label.
		assertEquals(Onboard.ON.getLabel(), "On");
		assertEquals(Onboard.OFF.getLabel(), "Off");
		// Test lookup(get).
		assertEquals(Onboard.get("Y"), Onboard.ON);
		assertEquals(Onboard.get("N"), Onboard.OFF);
	}
}
