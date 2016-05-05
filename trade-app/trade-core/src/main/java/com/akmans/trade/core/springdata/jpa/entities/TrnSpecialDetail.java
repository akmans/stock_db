package com.akmans.trade.core.springdata.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "trn_special_detail")
public class TrnSpecialDetail extends AbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long code;

	@Column(name = "name", length = 100)
    private String name;

	@Column(name = "detail", length = 300)
    private String detail;

	@Column(name = "amount")
    private Long amount;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "item_code")
	private TrnSpecialItem specialItem;

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

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getDetail() {
		return this.detail;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getAmount() {
		return this.amount;
	}

	public void setSpecialItem(TrnSpecialItem specialItem) {
		this.specialItem = specialItem;
	}

	public TrnSpecialItem getSpecialItem() {
		return this.specialItem;
	}

	@Override
    public String toString() {
        return this.getClass().getAnnotation(Table.class).name()
        		+ " [code=" + code
        		+ ", name=" + name
        		+ ", detail=" + detail
        		+ ", amount=" + amount
        		+ super.toString() + "] specialItem=[" + specialItem + "]";
    }
}
