package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class JapanStockJobTest {
	@Test
	public void testJapanStockJob() {
		// Test value.
		assertEquals(JapanStockJob.IMPORT_JAPAN_INSTRUMENT_JOB.toString(), "importJapanInstrumentJob");
		assertEquals(JapanStockJob.IMPORT_JAPAN_STOCK_JOB.toString(), "importJapanStockJob");
//		assertEquals(JapanStockJob.GENERATE_JAPAN_STOCK_WEEKLY_JOB.toString(), "generateJapanStockWeeklyJob");
//		assertEquals(JapanStockJob.GENERATE_JAPAN_STOCK_MONTHLY_JOB.toString(), "generateJapanStockMonthlyJob");
		assertEquals(JapanStockJob.IMPORT_JAPAN_INSTRUMENT_JOB.toString(),
				JapanStockJob.IMPORT_JAPAN_INSTRUMENT_JOB.getValue());
		assertEquals(JapanStockJob.IMPORT_JAPAN_STOCK_JOB.toString(), JapanStockJob.IMPORT_JAPAN_STOCK_JOB.getValue());
//		assertEquals(JapanStockJob.GENERATE_JAPAN_STOCK_WEEKLY_JOB.toString(),
//				JapanStockJob.GENERATE_JAPAN_STOCK_WEEKLY_JOB.getValue());
//		assertEquals(JapanStockJob.GENERATE_JAPAN_STOCK_MONTHLY_JOB.toString(),
//				JapanStockJob.GENERATE_JAPAN_STOCK_MONTHLY_JOB.getValue());
		// Test Label.
		assertEquals(JapanStockJob.IMPORT_JAPAN_INSTRUMENT_JOB.getLabel(), "0 : 日本銘柄導入処理");
		assertEquals(JapanStockJob.IMPORT_JAPAN_STOCK_JOB.getLabel(), "1 : 日本株ローソクデータ導入処理");
//		assertEquals(JapanStockJob.GENERATE_JAPAN_STOCK_WEEKLY_JOB.getLabel(), "2 : 日本株週足データ生成処理");
//		assertEquals(JapanStockJob.GENERATE_JAPAN_STOCK_MONTHLY_JOB.getLabel(), "3 : 日本株月足データ生成処理");
		// Test lookup(get).
		assertEquals(JapanStockJob.get("importJapanInstrumentJob"), JapanStockJob.IMPORT_JAPAN_INSTRUMENT_JOB);
		assertEquals(JapanStockJob.get("importJapanStockJob"), JapanStockJob.IMPORT_JAPAN_STOCK_JOB);
//		assertEquals(JapanStockJob.get("generateJapanStockWeeklyJob"), JapanStockJob.GENERATE_JAPAN_STOCK_WEEKLY_JOB);
//		assertEquals(JapanStockJob.get("generateJapanStockMonthlyJob"), JapanStockJob.GENERATE_JAPAN_STOCK_MONTHLY_JOB);
	}
}
