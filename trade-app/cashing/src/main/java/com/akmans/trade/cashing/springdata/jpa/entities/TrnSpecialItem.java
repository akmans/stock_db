package com.akmans.trade.cashing.springdata.jpa.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.akmans.trade.core.springdata.jpa.entities.AbstractEntity;

@Entity
@Table(name = "trn_special_item")
public class TrnSpecialItem extends AbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer code;

	@Column(name = "name", length = 100)
	private String name;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "code")
	private List<TrnSpecialDetail> specialDetails;

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

	public void setSpecialDetails(List<TrnSpecialDetail> specialDetails) {
		this.specialDetails = specialDetails;
	}

	public List<TrnSpecialDetail> getSpecialDetails() {
		return this.specialDetails;
	}

	@Override
	public String toString() {
		return this.getClass().getAnnotation(Table.class).name() + " [code=" + code + ", name=" + name
				+ super.toString() + "]";
	}
}
