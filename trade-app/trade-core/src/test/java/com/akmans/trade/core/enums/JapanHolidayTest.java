package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.akmans.trade.core.config.TestCoreConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestCoreConfig.class, loader = AnnotationConfigContextLoader.class)
public class JapanHolidayTest {
	@Test
	public void testJapanHoliday() {
		// Test value.
		assertEquals(JapanHoliday.NEW_YEARS_DAY.toString(), "10");
		assertEquals(JapanHoliday.COMING_OF_AGE_DAY.toString(), "20");
		assertEquals(JapanHoliday.NATIONAL_FOUDATION_DAY.toString(), "30");
		assertEquals(JapanHoliday.VERNAL_EQUINOX_DAY.toString(), "40");
		assertEquals(JapanHoliday.SHOWA_DAY.toString(), "50");
		assertEquals(JapanHoliday.CONSTITUTION_MEMORIAL_DAY.toString(), "60");
		assertEquals(JapanHoliday.GREENERY_DAY.toString(), "70");
		assertEquals(JapanHoliday.CHILDRENS_DAY.toString(), "80");
		assertEquals(JapanHoliday.MARINE_DAY.toString(), "90");
		assertEquals(JapanHoliday.MOUNTAIN_DAY.toString(), "100");
		assertEquals(JapanHoliday.RESPECT_FOR_THE_AGED_DAY.toString(), "110");
		assertEquals(JapanHoliday.NATIONAL_HOLIDAY.toString(), "120");
		assertEquals(JapanHoliday.AUTUMNAL_EQUINOX_DAY.toString(), "130");
		assertEquals(JapanHoliday.HEALTH_AND_SPORTS_DAY.toString(), "140");
		assertEquals(JapanHoliday.NATIONAL_AND_CULTRUE_DAY.toString(), "150");
		assertEquals(JapanHoliday.LABOR_THANKSGIVING_DAY.toString(), "160");
		assertEquals(JapanHoliday.EMPERORS_BIRTHDAY.toString(), "170");
		assertEquals(JapanHoliday.HOLIDAY_IN_LIEU.toString(), "180");
		assertEquals(JapanHoliday.NEW_YEARS_DAY.getValue(), 10);
		assertEquals(JapanHoliday.COMING_OF_AGE_DAY.getValue(), 20);
		assertEquals(JapanHoliday.NATIONAL_FOUDATION_DAY.getValue(), 30);
		assertEquals(JapanHoliday.VERNAL_EQUINOX_DAY.getValue(), 40);
		assertEquals(JapanHoliday.SHOWA_DAY.getValue(), 50);
		assertEquals(JapanHoliday.CONSTITUTION_MEMORIAL_DAY.getValue(), 60);
		assertEquals(JapanHoliday.GREENERY_DAY.getValue(), 70);
		assertEquals(JapanHoliday.CHILDRENS_DAY.getValue(), 80);
		assertEquals(JapanHoliday.MARINE_DAY.getValue(), 90);
		assertEquals(JapanHoliday.MOUNTAIN_DAY.getValue(), 100);
		assertEquals(JapanHoliday.RESPECT_FOR_THE_AGED_DAY.getValue(), 110);
		assertEquals(JapanHoliday.NATIONAL_HOLIDAY.getValue(), 120);
		assertEquals(JapanHoliday.AUTUMNAL_EQUINOX_DAY.getValue(), 130);
		assertEquals(JapanHoliday.HEALTH_AND_SPORTS_DAY.getValue(), 140);
		assertEquals(JapanHoliday.NATIONAL_AND_CULTRUE_DAY.getValue(), 150);
		assertEquals(JapanHoliday.LABOR_THANKSGIVING_DAY.getValue(), 160);
		assertEquals(JapanHoliday.EMPERORS_BIRTHDAY.getValue(), 170);
		assertEquals(JapanHoliday.HOLIDAY_IN_LIEU.getValue(), 180);
		// Test Label.
		assertEquals(JapanHoliday.NEW_YEARS_DAY.getLabel(), "元日");
		assertEquals(JapanHoliday.COMING_OF_AGE_DAY.getLabel(), "成人の日");
		assertEquals(JapanHoliday.NATIONAL_FOUDATION_DAY.getLabel(), "建国記念の日");
		assertEquals(JapanHoliday.VERNAL_EQUINOX_DAY.getLabel(), "春分の日");
		assertEquals(JapanHoliday.SHOWA_DAY.getLabel(), "昭和の日");
		assertEquals(JapanHoliday.CONSTITUTION_MEMORIAL_DAY.getLabel(), "憲法記念日");
		assertEquals(JapanHoliday.GREENERY_DAY.getLabel(), "みどりの日");
		assertEquals(JapanHoliday.CHILDRENS_DAY.getLabel(), "こどもの日");
		assertEquals(JapanHoliday.MARINE_DAY.getLabel(), "海の日");
		assertEquals(JapanHoliday.MOUNTAIN_DAY.getLabel(), "山の日");
		assertEquals(JapanHoliday.RESPECT_FOR_THE_AGED_DAY.getLabel(), "敬老の日");
		assertEquals(JapanHoliday.NATIONAL_HOLIDAY.getLabel(), "国民の休日");
		assertEquals(JapanHoliday.AUTUMNAL_EQUINOX_DAY.getLabel(), "秋分の日");
		assertEquals(JapanHoliday.HEALTH_AND_SPORTS_DAY.getLabel(), "体育の日");
		assertEquals(JapanHoliday.NATIONAL_AND_CULTRUE_DAY.getLabel(), "文化の日");
		assertEquals(JapanHoliday.LABOR_THANKSGIVING_DAY.getLabel(), "勤労感謝の日");
		assertEquals(JapanHoliday.EMPERORS_BIRTHDAY.getLabel(), "天皇誕生日");
		assertEquals(JapanHoliday.HOLIDAY_IN_LIEU.getLabel(), "振替休日");
		// Test lookup(get).
		assertEquals(JapanHoliday.get(10), JapanHoliday.NEW_YEARS_DAY);
		assertEquals(JapanHoliday.get(20), JapanHoliday.COMING_OF_AGE_DAY);
		assertEquals(JapanHoliday.get(30), JapanHoliday.NATIONAL_FOUDATION_DAY);
		assertEquals(JapanHoliday.get(40), JapanHoliday.VERNAL_EQUINOX_DAY);
		assertEquals(JapanHoliday.get(50), JapanHoliday.SHOWA_DAY);
		assertEquals(JapanHoliday.get(60), JapanHoliday.CONSTITUTION_MEMORIAL_DAY);
		assertEquals(JapanHoliday.get(70), JapanHoliday.GREENERY_DAY);
		assertEquals(JapanHoliday.get(80), JapanHoliday.CHILDRENS_DAY);
		assertEquals(JapanHoliday.get(90), JapanHoliday.MARINE_DAY);
		assertEquals(JapanHoliday.get(100), JapanHoliday.MOUNTAIN_DAY);
		assertEquals(JapanHoliday.get(110), JapanHoliday.RESPECT_FOR_THE_AGED_DAY);
		assertEquals(JapanHoliday.get(120), JapanHoliday.NATIONAL_HOLIDAY);
		assertEquals(JapanHoliday.get(130), JapanHoliday.AUTUMNAL_EQUINOX_DAY);
		assertEquals(JapanHoliday.get(140), JapanHoliday.HEALTH_AND_SPORTS_DAY);
		assertEquals(JapanHoliday.get(150), JapanHoliday.NATIONAL_AND_CULTRUE_DAY);
		assertEquals(JapanHoliday.get(160), JapanHoliday.LABOR_THANKSGIVING_DAY);
		assertEquals(JapanHoliday.get(170), JapanHoliday.EMPERORS_BIRTHDAY);
		assertEquals(JapanHoliday.get(180), JapanHoliday.HOLIDAY_IN_LIEU);
	}
}
