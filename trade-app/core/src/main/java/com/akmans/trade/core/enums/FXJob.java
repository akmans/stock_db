package com.akmans.trade.core.enums;

import java.util.HashMap;
import java.util.Map;

public enum FXJob {
	IMPORT_FX_TICK_JOB("0 : 為替データ導入処理", "importFXTickJob"), /*GENERATE_FX_HOUR_JOB("1 : 時間足生成処理",
			"generateFXHourJob"), GENERATE_FX_6HOUR_JOB("2 : 6時間足生成処理", "generateFX6HourJob"), GENERATE_FX_DAY_JOB(
					"3 : 日足生成処理",
					"generateFXDayJob"), GENERATE_FX_WEEK_JOB("4 : 週足生成処理", "generateFXWeekJob"), GENERATE_FX_MONTH_JOB(
							"5 : 月足生成処理", "generateFXMonthJob"),*/ GENERATE_FX_CANDLESTICK_DATA_JOB("6 : ローソク足データ生成処理",
									"generateFXCandlestickDataJob");

	private final String value;

	private final String label;

	// Reverse-lookup map for getting a day from an value.
	private static final Map<String, FXJob> lookup = new HashMap<String, FXJob>();

	static {
		for (FXJob c : FXJob.values()) {
			lookup.put(c.getValue(), c);
		}
	}

	private FXJob(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public static FXJob get(String value) {
		return lookup.get(value);
	}

	@Override
	public String toString() {
		return value;
	}
}
