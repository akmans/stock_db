package com.akmans.trade.fx.springdata.jpa.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;

import com.akmans.trade.core.springdata.jpa.entities.AbstractEntity;

@MappedSuperclass
public class AbstractFXTickEntity extends AbstractEntity {
	@Id
	@Column(name = "code", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long code;

	@Column(name = "currency_pair", nullable = false)
	private String currencyPair;

	@Column(name = "regist_date", nullable = false)
	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
	private LocalDateTime registDate;

	@Column(name = "bid_price")
	private double bidPrice;

	@Column(name = "ask_price")
	private double askPrice;

	@Column(name = "mid_price")
	private double midPrice;

	@Column(name = "processed_flag")
	private int processedFlag;

	public void setCode(Long code) {
		this.code = code;
	}

	public Long getCode() {
		return this.code;
	}

	public void setCurrencyPair(String currencyPair) {
		this.currencyPair = currencyPair;
	}

	public String getCurrencyPair() {
		return this.currencyPair;
	}

	public void setRegistDate(LocalDateTime registDate) {
		this.registDate = registDate;
	}

	public LocalDateTime getRegistDate() {
		return this.registDate;
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
		return " [code=" + code + ", currencyPair=" + currencyPair + ", registDate=" + registDate + ", bidPrice="
				+ bidPrice + ", askPrice=" + askPrice + ", midPrice=" + midPrice + ", processedFlag=" + processedFlag
				+ super.toString() + "]";
	}
}
