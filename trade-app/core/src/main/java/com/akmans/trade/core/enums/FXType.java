package com.akmans.trade.core.enums;

public enum FXType {
	HOUR("hour"), SIXHOUR("6hour"), DAY("day"), WEEK("week"), MONTH("month");

	private final String value;

	private FXType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
