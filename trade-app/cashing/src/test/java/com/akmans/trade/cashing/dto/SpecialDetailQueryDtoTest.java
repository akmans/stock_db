package com.akmans.trade.cashing.dto;

import com.akmans.trade.core.dto.DtoTest;

public class SpecialDetailQueryDtoTest extends DtoTest<SpecialDetailQueryDto> {

	public SpecialDetailQueryDtoTest() {
		super(null, null);
	}

	@Override
	protected SpecialDetailQueryDto getInstance() {
		return new SpecialDetailQueryDto();
	}
}
