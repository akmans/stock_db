package com.akmans.trade.core.enums;

public enum OperationStatus {
	ENTRY("ENTRY"), COMPLETE("COMPLETE");

	private final String value;

	private OperationStatus(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
