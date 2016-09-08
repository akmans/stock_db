package com.akmans.trade.core.enums;

import java.util.HashMap;
import java.util.Map;

public enum CurrencyPair {
	USDJPY("USD/JPY", "usdjpy"), EURJPY("EUR/JPY", "eurjpy"), AUDJPY("AUD/JPY", "audjpy"), GBPJPY("GBP/JPY",
			"gbpjpy"), CHFJPY("CHF/JPY", "chfjpy"), EURUSD("EUR/USD",
					"eurusd"), GBPUSD("GBP/USD", "gbpusd"), AUDUSD("AUD/USD", "audusd"), USDCHF("USD/CHF", "usdchf");

	private final String value;

	private final String label;

	// Reverse-lookup map for getting a day from an value.
	private static final Map<String, CurrencyPair> lookup = new HashMap<String, CurrencyPair>();

	static {
		for (CurrencyPair c : CurrencyPair.values()) {
			lookup.put(c.getValue(), c);
		}
	}

	private CurrencyPair(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public static CurrencyPair get(String value) {
		return lookup.get(value);
	}

	@Override
	public String toString() {
		return value;
	}
}
