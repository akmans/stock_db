package com.akmans.trade.stock.dto;

import com.akmans.trade.core.dto.DtoTest;

public class CsvJapanStockDtoTest extends DtoTest<CsvJapanStockDto> {

	public CsvJapanStockDtoTest() {
		super(null, null);
	}

	@Override
    protected CsvJapanStockDto getInstance() {
        return new CsvJapanStockDto();
    }
}
