package com.akmans.trade.stock.dto;

import com.akmans.trade.core.dto.DtoTest;

public class JapanStockLogQueryDtoTest extends DtoTest<JapanStockLogQueryDto> {

	public JapanStockLogQueryDtoTest() {
		super(null, null);
	}

	@Override
    protected JapanStockLogQueryDto getInstance() {
        return new JapanStockLogQueryDto();
    }
}
