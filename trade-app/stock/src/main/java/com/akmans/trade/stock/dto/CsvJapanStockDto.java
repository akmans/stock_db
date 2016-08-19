package com.akmans.trade.stock.dto;

public class CsvJapanStockDto {
	private String code;

	private String name;

	private String market;

	private String openingPrice;

	private String highPrice;

	private String lowPrice;

	private String finishPrice;

	private String turnover;

	private String tradingValue;

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getMarket() {
		return this.market;
	}

	public void setOpeningPrice(String openingPrice) {
		this.openingPrice = openingPrice;
	}

	public String getOpeningPrice() {
		return this.openingPrice;
	}

	public void setHighPrice(String highPrice) {
		this.highPrice = highPrice;
	}

	public String getHighPrice() {
		return this.highPrice;
	}

	public void setLowPrice(String lowPrice) {
		this.lowPrice = lowPrice;
	}

	public String getLowPrice() {
		return this.lowPrice;
	}

	public void setFinishPrice(String finishPrice) {
		this.finishPrice = finishPrice;
	}

	public String getFinishPrice() {
		return this.finishPrice;
	}

	public void setTurnover(String turnover) {
		this.turnover = turnover;
	}

	public String getTurnover() {
		return this.turnover;
	}

	public void setTradingValue(String tradingValue) {
		this.tradingValue = tradingValue;
	}

	public String getTradingValue() {
		return this.tradingValue;
	}

	@Override
	public String toString() {
		return "Stock [code=" + code + ", name=" + name + ", market=" + market + ", openingPrice=" + openingPrice
				+ ", highPrice=" + highPrice + ", lowPrice=" + lowPrice + ", finishPrice=" + finishPrice + ", turnover="
				+ turnover + ", tradingValue=" + tradingValue + "]";
	}
}
