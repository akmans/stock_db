package com.akmans.trade.fx.springdata.jpa.entities;

import com.akmans.trade.core.springdata.jpa.entities.AbstractEntity;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractFXEntity extends AbstractEntity {
	@EmbeddedId
	private FXTickKey tickKey;

	@Column(name = "opening_price")
	private double openingPrice;

	@Column(name = "high_price")
	private double highPrice;

	@Column(name = "low_price")
	private double lowPrice;

	@Column(name = "finish_price")
	private double finishPrice;

	public void setTickKey(FXTickKey tickKey) {
		this.tickKey = tickKey;
	}

	public FXTickKey getTickKey() {
		return this.tickKey;
	}

	public void setOpeningPrice(double openingPrice) {
		this.openingPrice = openingPrice;
	}

	public double getOpeningPrice() {
		return this.openingPrice;
	}

	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}

	public double getHighPrice() {
		return this.highPrice;
	}

	public void setLowPrice(double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public double getLowPrice() {
		return this.lowPrice;
	}

	public void setFinishPrice(double finishPrice) {
		this.finishPrice = finishPrice;
	}

	public double getFinishPrice() {
		return this.finishPrice;
	}

	@Override
	public String toString() {
		return " [tickKey=[" + tickKey.toString() + "], openingPrice=" + openingPrice + ", highPrice=" + highPrice
				+ ", lowPrice=" + lowPrice + ", finishPrice=" + finishPrice + super.toString() + "]";
	}
}
