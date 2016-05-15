package com.akmans.trade.core.springdata.jpa.keys;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;

@Embeddable
public class JapanStockKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "code", nullable = false)
	private Integer code;

	@Column(name = "regist_date", nullable = false)
	private Date registDate;

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return this.code;
	}

	public void setRegistDate(Date registDate) {
		this.registDate = registDate;
	}

	public Date getRegistDate() {
		return this.registDate;
	}

	@Override
	public String toString() {
		return "[code=" + code + ", regist_date=" + registDate + "]";
	}
}