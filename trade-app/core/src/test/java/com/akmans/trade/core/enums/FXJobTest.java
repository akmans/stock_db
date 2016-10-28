package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class FXJobTest {
	@Test
	public void testFXJob() {
		// Test value.
		assertEquals(FXJob.GENERATE_FX_CANDLESTICK_DATA_JOB.toString(), "generateFXCandlestickDataJob");
		// Test Label.
		assertEquals(FXJob.GENERATE_FX_CANDLESTICK_DATA_JOB.getLabel(), "ローソク足データ生成処理");
		// Test lookup(get).
		assertEquals(FXJob.get("generateFXCandlestickDataJob"), FXJob.GENERATE_FX_CANDLESTICK_DATA_JOB);
	}
}
