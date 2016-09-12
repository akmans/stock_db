package com.akmans.trade.fx.springdata.jpa.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.akmans.trade.core.springdata.jpa.entities.AbstractEntity;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

@Entity
@Table(name = "trn_fx_tick")
public class TrnFXTick extends AbstractEntity {
	@EmbeddedId
	private FXTickKey tickKey;

	@Column(name = "bid_price")
	private double bidPrice;

	@Column(name = "ask_price")
	private double askPrice;

	@Column(name = "mid_price")
	private double midPrice;

	@Column(name = "processed_flag")
	private int processedFlag;

	public void setTickKey(FXTickKey tickKey) {
		this.tickKey = tickKey;
	}

	public FXTickKey getTickKey() {
		return this.tickKey;
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

	public void setProcessedFlag(int processedFlag) {
		this.processedFlag = processedFlag;
	}

	public int getProcessedFlag() {
		return this.processedFlag;
	}

	@Override
	public String toString() {
		return this.getClass().getAnnotation(Table.class).name() + " [tickKey=[" + tickKey.toString() + "], bidPrice="
				+ bidPrice + ", askPrice=" + askPrice + ", midPrice=" + midPrice + ", processedFlag=" + processedFlag
				+ super.toString() + "]";
	}
}
