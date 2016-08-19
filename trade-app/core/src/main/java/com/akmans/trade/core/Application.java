package com.akmans.trade.core;

import com.akmans.trade.core.enums.RunningMode;

public class Application {
	private RunningMode runningMode = null;

	public void setRunningMode(RunningMode runningMode) {
		this.runningMode = runningMode;
	}

	public RunningMode getRunningMode() {
		return this.runningMode;
	}
}
