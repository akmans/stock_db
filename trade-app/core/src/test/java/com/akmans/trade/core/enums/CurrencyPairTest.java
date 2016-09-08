package com.akmans.trade.core.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class CurrencyPairTest {
	@Test
	public void testCurrencyPair() {
		// Test value.
		assertEquals(CurrencyPair.AUDJPY.toString(), "audjpy");
		assertEquals(CurrencyPair.AUDUSD.toString(), "audusd");
		assertEquals(CurrencyPair.CHFJPY.toString(), "chfjpy");
		assertEquals(CurrencyPair.EURJPY.toString(), "eurjpy");
		assertEquals(CurrencyPair.EURUSD.toString(), "eurusd");
		assertEquals(CurrencyPair.GBPJPY.toString(), "gbpjpy");
		assertEquals(CurrencyPair.GBPUSD.toString(), "gbpusd");
		assertEquals(CurrencyPair.USDCHF.toString(), "usdchf");
		assertEquals(CurrencyPair.USDJPY.toString(), "usdjpy");
		// Test Label.
		assertEquals(CurrencyPair.AUDJPY.getLabel(), "AUD/JPY");
		assertEquals(CurrencyPair.AUDUSD.getLabel(), "AUD/USD");
		assertEquals(CurrencyPair.CHFJPY.getLabel(), "CHF/JPY");
		assertEquals(CurrencyPair.EURJPY.getLabel(), "EUR/JPY");
		assertEquals(CurrencyPair.EURUSD.getLabel(), "EUR/USD");
		assertEquals(CurrencyPair.GBPJPY.getLabel(), "GBP/JPY");
		assertEquals(CurrencyPair.GBPUSD.getLabel(), "GBP/USD");
		assertEquals(CurrencyPair.USDCHF.getLabel(), "USD/CHF");
		assertEquals(CurrencyPair.USDJPY.getLabel(), "USD/JPY");
		// Test lookup(get).
		assertEquals(CurrencyPair.get("audjpy"), CurrencyPair.AUDJPY);
		assertEquals(CurrencyPair.get("audusd"), CurrencyPair.AUDUSD);
		assertEquals(CurrencyPair.get("chfjpy"), CurrencyPair.CHFJPY);
		assertEquals(CurrencyPair.get("eurjpy"), CurrencyPair.EURJPY);
		assertEquals(CurrencyPair.get("eurusd"), CurrencyPair.EURUSD);
		assertEquals(CurrencyPair.get("gbpjpy"), CurrencyPair.GBPJPY);
		assertEquals(CurrencyPair.get("gbpusd"), CurrencyPair.GBPUSD);
		assertEquals(CurrencyPair.get("usdchf"), CurrencyPair.USDCHF);
		assertEquals(CurrencyPair.get("usdjpy"), CurrencyPair.USDJPY);
	}
}
