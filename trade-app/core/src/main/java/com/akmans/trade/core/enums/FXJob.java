package com.akmans.trade.core.enums;

import java.util.HashMap;
import java.util.Map;

public enum FXJob {
	IMPORT_HISTORY_TICK_JOB("0 : 過去為替データ導入処理", "importHistoryTickJob");

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
