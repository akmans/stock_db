package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class RunningModeTest {
	@Test
	public void testRunningMode() {
		// Test value.
		assertEquals(RunningMode.WEB.toString(), "Web");
		assertEquals(RunningMode.STANDALONE.toString(), "Standalone");
	}
}
