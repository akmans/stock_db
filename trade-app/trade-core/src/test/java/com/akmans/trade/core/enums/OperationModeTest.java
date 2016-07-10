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
public class OperationModeTest {
	@Test
	public void testOperationMode() {
		// Test value.
		assertEquals(OperationMode.NEW.toString(), "NEW");
		assertEquals(OperationMode.EDIT.toString(), "EDIT");
		assertEquals(OperationMode.DELETE.toString(), "DELETE");
	}
}
