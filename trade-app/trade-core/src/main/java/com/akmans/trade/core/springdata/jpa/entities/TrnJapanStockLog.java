package com.akmans.trade.core.springdata.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.akmans.trade.core.springdata.jpa.keys.JapanStockLogKey;

@Entity
@Table(name = "trn_japan_stock_log")
public class TrnJapanStockLog extends AbstractEntity {

	@Id
	private JapanStockLogKey japanStockLogKey;

	public void setJapanStockLogKey(JapanStockLogKey japanStockLogKey) {
		this.japanStockLogKey = japanStockLogKey;
	}

	public JapanStockLogKey getJapanStockLogKey() {
		return this.japanStockLogKey;
	}

	@Override
	public String toString() {
		return this.getClass().getAnnotation(Table.class).name() + " [primary key=" + japanStockLogKey.toString()
				+ "] [" + super.toString() + "]";
	}
}
