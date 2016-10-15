package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class FXJobTest {
	@Test
	public void testFXJob() {
		// Test value.
		assertEquals(FXJob.IMPORT_FX_TICK_JOB.toString(), "importFXTickJob");
		// Test Label.
		assertEquals(FXJob.IMPORT_FX_TICK_JOB.getLabel(), "0 : 為替データ導入処理");
		// Test lookup(get).
		assertEquals(FXJob.get("importFXTickJob"), FXJob.IMPORT_FX_TICK_JOB);
		// Test value.
		assertEquals(FXJob.GENERATE_FX_CANDLESTICK_DATA_JOB.toString(), "generateFXCandlestickDataJob");
		// Test Label.
		assertEquals(FXJob.GENERATE_FX_CANDLESTICK_DATA_JOB.getLabel(), "6 : ローソク足データ生成処理");
		// Test lookup(get).
		assertEquals(FXJob.get("generateFXCandlestickDataJob"), FXJob.GENERATE_FX_CANDLESTICK_DATA_JOB);
	}
}
