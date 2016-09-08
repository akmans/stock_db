package com.akmans.trade.fx.springdata.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "trn_fx_month")
public class TrnFXMonth extends AbstractFXEntity {
	@Override
	public String toString() {
		return this.getClass().getAnnotation(Table.class).name() + super.toString();
	}
}
