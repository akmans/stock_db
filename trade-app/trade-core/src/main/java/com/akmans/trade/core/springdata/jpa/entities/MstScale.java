package com.akmans.trade.core.springdata.jpa.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "mst_scale")
public class MstScale extends AbstractEntity {

	@Id
	private Integer code;

	@Column(name = "name", length = 100)
    private String name;

	@OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="code")
	private List<MstInstrument> instruments;

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

	public void setInstruments(List<MstInstrument> instruments) {
		this.instruments = instruments;
	}

	public List<MstInstrument> getInstruments() {
		return this.instruments;
	}

	@Override
    public String toString() {
        return this.getClass().getAnnotation(Table.class).name()
        		+ " [code=" + code
        		+ ", name=" + name
        		+ super.toString() + "]";
    }
}
