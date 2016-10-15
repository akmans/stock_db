package com.akmans.trade.fx.dto;

public class CsvFXTickDto {
	private String currencyPair;

	private String millisecond;

	private String bidBig;

	private String bidSmall;

	private String askBig;

	private String askSmall;

	public void setCurrencyPair(String currencyPair) {
		this.currencyPair = currencyPair;
	}

	public String getCurrencyPair() {
		return this.currencyPair;
	}

	public void setMillisecond(String millisecond) {
		this.millisecond = millisecond;
	}

	public String getMillisecond() {
		return this.millisecond;
	}

	public void setBidBig(String bidBig) {
		this.bidBig = bidBig;
	}

	public String getBidBig() {
		return this.bidBig;
	}

	public void setBidSmall(String bidSmall) {
		this.bidSmall = bidSmall;
	}

	public String getBidSmall() {
		return this.bidSmall;
	}

	public void setAskBig(String askBig) {
		this.askBig = askBig;
	}

	public String getAskBig() {
		return this.askBig;
	}

	public void setAskSmall(String askSmall) {
		this.askSmall = askSmall;
	}

	public String getAskSmall() {
		return this.askSmall;
	}

	@Override
	public String toString() {
		return "Csv Currency Tick [currencyPair=" + currencyPair + ", millisecond=" + millisecond + ", bidBig=" + bidBig
				+ ", bidSmall=" + bidSmall + ", askBig=" + askBig + ", askSmall=" + askSmall + "]";
	}
}
