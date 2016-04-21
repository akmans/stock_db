package com.akmans.trade.core.springdata.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "trn_loaded_log")
public class TrnLoadedLog {

	@Id
	private Integer code;

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return this.code;
	}

	@Override
    public String toString() {
        return this.getClass().getAnnotation(Table.class).name()
        		+ " [code=" + code + "]";
    }
}
