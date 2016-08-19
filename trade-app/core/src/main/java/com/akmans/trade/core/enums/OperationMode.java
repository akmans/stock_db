package com.akmans.trade.core.enums;

public enum OperationMode {
	NEW("NEW"), EDIT("EDIT"), DELETE("DELETE");

	private final String value;

	private OperationMode(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
