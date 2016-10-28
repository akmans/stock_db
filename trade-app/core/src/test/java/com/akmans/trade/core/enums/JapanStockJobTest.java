package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class JapanStockJobTest {
	@Test
	public void testJapanStockJob() {
		// Test value.
		assertEquals(JapanStockJob.IMPORT_JAPAN_INSTRUMENT_JOB.toString(), "importJapanInstrumentJob");
		assertEquals(JapanStockJob.IMPORT_JAPAN_STOCK_JOB.toString(), "importJapanStockJob");
		assertEquals(JapanStockJob.IMPORT_JAPAN_INSTRUMENT_JOB.toString(),
				JapanStockJob.IMPORT_JAPAN_INSTRUMENT_JOB.getValue());
		assertEquals(JapanStockJob.IMPORT_JAPAN_STOCK_JOB.toString(), JapanStockJob.IMPORT_JAPAN_STOCK_JOB.getValue());
		// Test Label.
		assertEquals(JapanStockJob.IMPORT_JAPAN_INSTRUMENT_JOB.getLabel(), "日本銘柄導入処理");
		assertEquals(JapanStockJob.IMPORT_JAPAN_STOCK_JOB.getLabel(), "日本株ローソクデータ導入処理");
		// Test lookup(get).
		assertEquals(JapanStockJob.get("importJapanInstrumentJob"), JapanStockJob.IMPORT_JAPAN_INSTRUMENT_JOB);
		assertEquals(JapanStockJob.get("importJapanStockJob"), JapanStockJob.IMPORT_JAPAN_STOCK_JOB);
	}
}
