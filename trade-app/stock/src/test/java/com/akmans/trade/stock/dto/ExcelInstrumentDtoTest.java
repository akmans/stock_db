package com.akmans.trade.stock.dto;

import com.akmans.trade.core.dto.DtoTest;

public class ExcelInstrumentDtoTest extends DtoTest<ExcelInstrumentDto> {

	public ExcelInstrumentDtoTest() {
		super(null, null);
	}

	@Override
    protected ExcelInstrumentDto getInstance() {
        return new ExcelInstrumentDto();
    }
}
