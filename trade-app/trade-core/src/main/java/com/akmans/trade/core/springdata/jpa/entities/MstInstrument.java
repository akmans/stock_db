package com.akmans.trade.core.springdata.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mst_instrument")
public class MstInstrument {

	@Id
	@Column(name = "code")
	private Integer code;

	@Column(name = "name", length = 100)
    private String name;

	@Column(name = "sector33_code")
	private Integer sector33;

	@Column(name = "sector17_code")
	private Integer sector17;

	@Column(name = "scale_code")
	private Integer scale;

	@Column(name = "market_code")
	private Integer market;

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return this.code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setSector33(Integer sector33) {
		this.sector33 = sector33;
	}

	public Integer getSector33() {
		return this.sector33;
	}

	public void setSector17(Integer sector17) {
		this.sector17 = sector17;
	}

	public Integer getSector17() {
		return this.sector17;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public Integer getScale() {
		return this.scale;
	}

	public void setMarket(Integer market) {
		this.market = market;
	}

	public Integer getMarket() {
		return this.market;
	}

	@Override
    public String toString() {
        return this.getClass().getAnnotation(Table.class).name()
        		+ " [code=" + code
        		+ ", name=" + name
        		+ ", sector33_code=" + sector33
        		+ ", sector17_code=" + sector17
        		+ ", scale_code=" + scale
        		+ ", market_code=" + market + "]";
    }
}
