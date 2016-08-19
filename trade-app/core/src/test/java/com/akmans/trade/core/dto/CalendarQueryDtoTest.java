package com.akmans.trade.core.dto;

public class CalendarQueryDtoTest extends DtoTest<CalendarQueryDto> {

	public CalendarQueryDtoTest() {
		super(null, null);
	}

	@Override
    protected CalendarQueryDto getInstance() {
        return new CalendarQueryDto();
    }
}
