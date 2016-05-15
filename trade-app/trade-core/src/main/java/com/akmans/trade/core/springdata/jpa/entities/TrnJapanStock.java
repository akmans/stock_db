package com.akmans.trade.core.springdata.jpa.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.akmans.trade.core.springdata.jpa.keys.JapanStockKey;

@Entity
@Table(name = "trn_japan_stock")
public class TrnJapanStock extends AbstractEntity {

	@EmbeddedId
	private JapanStockKey japanStockKey;

	@Column(name = "opening_price")
	private Integer openingPrice;

	@Column(name = "high_price")
	private Integer highPrice;

	@Column(name = "low_price")
	private Integer lowPrice;

	@Column(name = "finish_price")
	private Integer finishPrice;

	@Column(name = "turnover")
	private Long turnover;

	@Column(name = "trading_value")
	private Long tradingValue;

	public void setJapanStockKey(JapanStockKey japanStockKey) {
		this.japanStockKey = japanStockKey;
	}

	public JapanStockKey getJapanStockKey() {
		return this.japanStockKey;
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

	public void setTurnover(Long turnover) {
		this.turnover = turnover;
	}

	public Long getTurnover() {
		return this.turnover;
	}

	public void setTradingValue(Long tradingValue) {
		this.tradingValue = tradingValue;
	}

	public Long getTradingValue() {
		return this.tradingValue;
	}

	@Override
	public String toString() {
		return this.getClass().getAnnotation(Table.class).name() + " [code=" + japanStockKey.getCode()
				+ ", regist_date=" + japanStockKey.getRegistDate() + ", opening_price=" + openingPrice + ", high_price="
				+ highPrice + ", low_price=" + lowPrice + ", finish_price=" + finishPrice + ", turnover=" + turnover
				+ ", trading_value=" + tradingValue + super.toString() + "]";
	}
}
