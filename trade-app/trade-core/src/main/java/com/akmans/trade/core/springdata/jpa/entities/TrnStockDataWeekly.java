package com.akmans.trade.core.springdata.jpa.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.akmans.trade.core.springdata.jpa.keys.StockDataKey;

@Entity
@Table(name = "trn_stock_weekly")
public class TrnStockDataWeekly {

	@EmbeddedId
	private StockDataKey stockDataKey;

	@Column(name = "opening_price")
	private Integer openingPrice;

	@Column(name = "high_price")
	private Integer highPrice;

	@Column(name = "low_price")
	private Integer lowPrice;

	@Column(name = "finish_price")
	private Integer finishPrice;

	@Column(name = "turnover")
	private Integer turnover;

	public void setStockDataKey(StockDataKey stockDataKey) {
		this.stockDataKey = stockDataKey;
	}

	public StockDataKey getStockDataKey() {
		return this.stockDataKey;
	}

	public void setOpeningPrice(Integer openingPrice) {
		this.openingPrice = openingPrice;
	}

	public Integer getOpeningPrice() {
		return this.openingPrice;
	}

	public void setHighPrice(Integer highPrice) {
		this.highPrice = highPrice;
	}

	public Integer getHighPrice() {
		return this.highPrice;
	}

	public void setLowPrice(Integer lowPrice) {
		this.lowPrice = lowPrice;
	}

	public Integer getLowPrice() {
		return this.lowPrice;
	}

	public void setFinishPrice(Integer finishPrice) {
		this.finishPrice = finishPrice;
	}

	public Integer getFinishPrice() {
		return this.finishPrice;
	}

	public void setTurnover(Integer turnover) {
		this.turnover = turnover;
	}

	public Integer getTurnover() {
		return this.turnover;
	}

	@Override
    public String toString() {
        return this.getClass().getAnnotation(Table.class).name()
        		+ " [code=" + stockDataKey.getCode()
        		+ ", regist_date=" + stockDataKey.getRegistDate()
        		+ ", opening_price=" + openingPrice
        		+ ", high_price=" + highPrice
        		+ ", low_price=" + lowPrice
        		+ ", finish_price=" + finishPrice
        		+ ", turnover=" + turnover
        		+ "]";
    }
}
