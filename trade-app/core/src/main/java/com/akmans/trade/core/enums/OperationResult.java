package com.akmans.trade.core.enums;

public enum OperationResult {
	NONE("none"), INSERTED("inserted"), UPDATED("updated");

	private final String value;

	private OperationResult(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
