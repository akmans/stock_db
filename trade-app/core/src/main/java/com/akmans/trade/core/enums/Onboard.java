package com.akmans.trade.core.enums;

import java.util.HashMap;
import java.util.Map;

public enum Onboard {
	ON("On", "Y"), OFF("Off", "N");

	private final String value;

	private final String label;

	// Reverse-lookup map for getting a day from an value.
	private static final Map<String, Onboard> lookup = new HashMap<String, Onboard>();

	static {
		for (Onboard c : Onboard.values()) {
			lookup.put(c.getValue(), c);
		}
	}

	private Onboard(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public static Onboard get(String value) {
		return lookup.get(value);
	}

	@Override
	public String toString() {
		return value;
	}
}
