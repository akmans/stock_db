package com.akmans.trade.core.enums;

import java.util.HashMap;
import java.util.Map;

public enum JapanHoliday {
	NEW_YEARS_DAY("元日", 10),
	COMING_OF_AGE_DAY("成人の日", 20),
	NATIONAL_FOUDATION_DAY("建国記念の日", 30),
	VERNAL_EQUINOX_DAY("春分の日", 40),
	SHOWA_DAY("昭和の日", 50),
	CONSTITUTION_MEMORIAL_DAY("憲法記念日", 60),
	GREENERY_DAY("みどりの日", 70),
	CHILDRENS_DAY("こどもの日", 80),
	MARINE_DAY("海の日", 90),
	MOUNTAIN_DAY("山の日", 100),
	RESPECT_FOR_THE_AGED_DAY("敬老の日", 110),
	NATIONAL_HOLIDAY("国民の休日", 120),
	AUTUMNAL_EQUINOX_DAY("秋分の日", 130),
	HEALTH_AND_SPORTS_DAY("体育の日", 140),
	NATIONAL_AND_CULTRUE_DAY("文化の日", 150),
	LABOR_THANKSGIVING_DAY("勤労感謝の日", 160),
	EMPERORS_BIRTHDAY("天皇誕生日", 170),
	HOLIDAY_IN_LIEU("振替休日", 180);

	private final int value;

	private final String label;

	// Reverse-lookup map for getting a day from an value.
	private static final Map<Integer, JapanHoliday> lookup = new HashMap<Integer, JapanHoliday>();

	static {
        for (JapanHoliday h : JapanHoliday.values()) {
            lookup.put(h.getValue(), h);
        }
    }

	private JapanHoliday(String label, int value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public int getValue() {
		return value;
	}

	public static JapanHoliday get(Integer value) {
        return lookup.get(value);
    }

	@Override
	public String toString() {
		return Integer.toString(value);
	}
}
