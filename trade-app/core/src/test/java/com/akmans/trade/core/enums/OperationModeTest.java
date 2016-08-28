package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class OperationModeTest {
	@Test
	public void testOperationMode() {
		// Test value.
		assertEquals(OperationMode.NEW.toString(), "NEW");
		assertEquals(OperationMode.EDIT.toString(), "EDIT");
		assertEquals(OperationMode.DELETE.toString(), "DELETE");
	}
}
