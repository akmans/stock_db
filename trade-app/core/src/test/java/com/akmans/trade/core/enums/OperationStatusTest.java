package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class OperationStatusTest {
	@Test
	public void testOperationStatus() {
		// Test value.
		assertEquals(OperationStatus.ENTRY.toString(), "ENTRY");
		assertEquals(OperationStatus.COMPLETE.toString(), "COMPLETE");
	}
}
