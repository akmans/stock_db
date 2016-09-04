package com.akmans.trade.stock.model;

import com.akmans.trade.core.dto.DtoTest;

public class StockTest extends DtoTest<Stock> {

	public StockTest() {
		super(null, null);
	}

	@Override
    protected Stock getInstance() {
        return new Stock();
    }
}
