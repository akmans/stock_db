package com.akmans.trade.core.enums;

import java.util.HashMap;
import java.util.Map;

public enum Calendar {
	CHINA("China", "cn"), JAPAN("Japan", "jp");

	private final String value;

	private final String label;

	// Reverse-lookup map for getting a day from an value.
	private static final Map<String, Calendar> lookup = new HashMap<String, Calendar>();

	static {
        for (Calendar c : Calendar.values()) {
            lookup.put(c.getValue(), c);
        }
    }

	private Calendar(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public static Calendar get(String value) {
        return lookup.get(value);
    }

	@Override
	public String toString() {
		return value;
	}
}
