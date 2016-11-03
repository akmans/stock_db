package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class OperationResultTest {
	@Test
	public void testOperationResult() {
		// Test value.
		assertEquals(OperationResult.NONE.toString(), "none");
		assertEquals(OperationResult.INSERTED.toString(), "inserted");
		assertEquals(OperationResult.UPDATED.toString(), "updated");
	}
}
