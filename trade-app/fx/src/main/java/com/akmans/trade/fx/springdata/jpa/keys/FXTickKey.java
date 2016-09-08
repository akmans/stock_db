package com.akmans.trade.fx.springdata.jpa.keys;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;

@Embeddable
public class FXTickKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "currency_pair", nullable = false)
	private String currencyPair;

	@Column(name = "regist_date", nullable = false)
	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
	private ZonedDateTime registDate;

	public void setCurrencyPair(String currencyPair) {
		this.currencyPair = currencyPair;
	}

	public String getCurrencyPair() {
		return this.currencyPair;
	}

	public void setRegistDate(ZonedDateTime registDate) {
		this.registDate = registDate;
	}

	public ZonedDateTime getRegistDate() {
		return this.registDate;
	}

	@Override
	public String toString() {
		return "[currency_pair=" + currencyPair + ", regist_date=" + registDate + "]";
	}
}