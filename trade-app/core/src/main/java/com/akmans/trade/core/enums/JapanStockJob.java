package com.akmans.trade.core.enums;

import java.util.HashMap;
import java.util.Map;

public enum JapanStockJob {
	IMPORT_JAPAN_INSTRUMENT_JOB("0 : 日本銘柄導入処理", "importJapanInstrumentJob"), IMPORT_JAPAN_STOCK_JOB("1 : 日本株ローソクデータ導入処理",
			"importJapanStockJob")/*, GENERATE_JAPAN_STOCK_WEEKLY_JOB("2 : 日本株週足データ生成処理",
					"generateJapanStockWeeklyJob"), GENERATE_JAPAN_STOCK_MONTHLY_JOB("3 : 日本株月足データ生成処理",
							"generateJapanStockMonthlyJob")*/;

	private final String value;

	private final String label;

	// Reverse-lookup map for getting a day from an value.
	private static final Map<String, JapanStockJob> lookup = new HashMap<String, JapanStockJob>();

	static {
		for (JapanStockJob c : JapanStockJob.values()) {
			lookup.put(c.getValue(), c);
		}
	}

	private JapanStockJob(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public static JapanStockJob get(String value) {
		return lookup.get(value);
	}

	@Override
	public String toString() {
		return value;
	}
}
