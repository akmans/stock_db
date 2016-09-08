package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class FXJobTest {
	@Test
	public void testFXJob() {
		// Test value.
		assertEquals(FXJob.IMPORT_HISTORY_TICK_JOB.toString(), "importHistoryTickJob");
		// Test Label.
		assertEquals(FXJob.IMPORT_HISTORY_TICK_JOB.getLabel(), "0 : 過去為替データ導入処理");
		// Test lookup(get).
		assertEquals(FXJob.get("importHistoryTickJob"), FXJob.IMPORT_HISTORY_TICK_JOB);
	}
}
