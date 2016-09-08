package com.akmans.trade.fx.model;

import java.time.ZonedDateTime;

public class FXTick {

	private String currencyPair;

	private ZonedDateTime registDate;

	private double bidPrice;

	private double askPrice;

	private double midPrice;

	public void setCurrencyPair(String currencyPair) {
		this.currencyPair = currencyPair;
	}

	public String getCurrencyPair() {
		return this.currencyPair;
	}

	public void setRegistDate(ZonedDateTime registDate) {
		this.registDate = registDate;
	}

	public ZonedDateTime getRegistDate() {
		return this.registDate;
	}

	public void setBidPrice(double bidPrice) {
		this.bidPrice = bidPrice;
	}

	public double getBidPrice() {
		return this.bidPrice;
	}

	public void setAskPrice(double askPrice) {
		this.askPrice = askPrice;
	}

	public double getAskPrice() {
		return this.askPrice;
	}

	public void setMidPrice(double midPrice) {
		this.midPrice = midPrice;
	}

	public double getMidPrice() {
		return this.midPrice;
	}

	@Override
	public String toString() {
		return "Csv Currency Tick [currencyPair=" + currencyPair + ", registDate=" + registDate + ", bidPrice="
				+ bidPrice + ", askPrice=" + askPrice + ", midPrice=" + midPrice + "]";
	}
}
