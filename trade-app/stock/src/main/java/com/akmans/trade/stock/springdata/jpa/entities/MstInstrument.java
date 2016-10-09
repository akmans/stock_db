package com.akmans.trade.stock.springdata.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.akmans.trade.core.springdata.jpa.entities.AbstractEntity;

@Entity
@Table(name = "mst_instrument")
public class MstInstrument extends AbstractEntity {

	@Id
	@Column(name = "code")
	private Long code;

	@Column(name = "name", length = 100)
	private String name;

	@ManyToOne//(fetch = FetchType.LAZY)
	@JoinColumn(name = "sector33_code")
	private MstSector33 sector33;

	@ManyToOne//(fetch = FetchType.LAZY)
	@JoinColumn(name = "sector17_code")
	private MstSector17 sector17;

	@ManyToOne//(fetch = FetchType.LAZY)
	@JoinColumn(name = "scale_code")
	private MstScale scale;

	@ManyToOne//(fetch = FetchType.LAZY)
	@JoinColumn(name = "market_code")
	private MstMarket market;

	@Column(name = "onboard", length = 1)
	private String onboard;

	public void setCode(Long code) {
		this.code = code;
	}

	public Long getCode() {
		return this.code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setSector33(MstSector33 sector33) {
		this.sector33 = sector33;
	}

	public MstSector33 getSector33() {
		return this.sector33;
	}

	public void setSector17(MstSector17 sector17) {
		this.sector17 = sector17;
	}

	public MstSector17 getSector17() {
		return this.sector17;
	}

	public void setScale(MstScale scale) {
		this.scale = scale;
	}

	public MstScale getScale() {
		return this.scale;
	}

	public void setMarket(MstMarket market) {
		this.market = market;
	}

	public MstMarket getMarket() {
		return this.market;
	}

	public void setOnboard(String onboard) {
		this.onboard = onboard;
	}

	public String getOnboard() {
		return this.onboard;
	}

	@Override
	public String toString() {
		return this.getClass().getAnnotation(Table.class).name() + " [code=" + code + ", name=" + name + ", onboard="
				+ onboard + super.toString() + "] scale=[" + scale + "] market=[" + market + "] sector17=[" + sector17
				+ "] sector33=[" + sector33 + "]";
	}
}
