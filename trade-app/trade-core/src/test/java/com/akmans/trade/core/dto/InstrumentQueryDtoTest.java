package com.akmans.trade.core.dto;

public class InstrumentQueryDtoTest extends DtoTest<InstrumentQueryDto> {

	public InstrumentQueryDtoTest() {
		super(null, null);
	}

	@Override
    protected InstrumentQueryDto getInstance() {
        return new InstrumentQueryDto();
    }
}
