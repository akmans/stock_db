package com.akmans.trade.core.enums;

public enum RunningMode {
	WEB("Web"), STANDALONE("Standalone");

	private final String value;

	private RunningMode(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
