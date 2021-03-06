package com.akmans.trade.fx.springdata.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "trn_fx_tick")
public class TrnFXTick extends AbstractFXTickEntity {
	@Override
	public String toString() {
		return this.getClass().getAnnotation(Table.class).name() + super.toString();
	}
}
