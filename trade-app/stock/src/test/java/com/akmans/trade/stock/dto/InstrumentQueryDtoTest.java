package com.akmans.trade.stock.dto;

import com.akmans.trade.core.dto.DtoTest;

public class InstrumentQueryDtoTest extends DtoTest<InstrumentQueryDto> {

	public InstrumentQueryDtoTest() {
		super(null, null);
	}

	@Override
    protected InstrumentQueryDto getInstance() {
        return new InstrumentQueryDto();
    }
}
