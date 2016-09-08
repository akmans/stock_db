package com.akmans.trade.fx.dto;

public class CsvHistoryTickDto {
	private String currencyPair;

	private String registDate;

	private String bidPrice;

	private String askPrice;

	public void setCurrencyPair(String currencyPair) {
		this.currencyPair = currencyPair;
	}

	public String getCurrencyPair() {
		return this.currencyPair;
	}

	public void setRegistDate(String registDate) {
		this.registDate = registDate;
	}

	public String getRegistDate() {
		return this.registDate;
	}

	public void setBidPrice(String bidPrice) {
		this.bidPrice = bidPrice;
	}

	public String getBidPrice() {
		return this.bidPrice;
	}

	public void setAskPrice(String askPrice) {
		this.askPrice = askPrice;
	}

	public String getAskPrice() {
		return this.askPrice;
	}

	@Override
	public String toString() {
		return "Csv Currency Tick [currencyPair=" + currencyPair + ", registDate=" + registDate + ", bidPrice="
				+ bidPrice + ", askPrice=" + askPrice + "]";
	}
}
