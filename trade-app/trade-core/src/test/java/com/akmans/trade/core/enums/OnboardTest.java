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
public class OnboardTest {
	@Test
	public void testOnboard() {
		// Test value.
		assertEquals(Onboard.ON.toString(), "Y");
		assertEquals(Onboard.OFF.toString(), "N");
		assertEquals(Onboard.ON.toString(), Onboard.ON.getValue());
		assertEquals(Onboard.OFF.toString(), Onboard.OFF.getValue());
		// Test Label.
		assertEquals(Onboard.ON.getLabel(), "On");
		assertEquals(Onboard.OFF.getLabel(), "Off");
		// Test lookup(get).
		assertEquals(Onboard.get("Y"), Onboard.ON);
		assertEquals(Onboard.get("N"), Onboard.OFF);
	}
}
