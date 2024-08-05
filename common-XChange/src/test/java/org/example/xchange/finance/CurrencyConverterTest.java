package org.example.xchange.finance;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrencyConverterTest {


	@Test
	void converUsdt(){
		BigDecimal priceBTC = new BigDecimal("67773.2");
		BigDecimal cash = new BigDecimal("67.74");
		BigDecimal expectedBTC = new BigDecimal("0.00100");
		BigDecimal usdt = CurrencyConverter.convertCurrency(priceBTC, cash);
		assertEquals(expectedBTC, usdt);
	}
	@Test
	void convertInUsdtpoPriceAndSumma(){
		BigDecimal bigDecimal = CurrencyConverter.convertUsdt(new BigDecimal("1"), new BigDecimal("68000"));
		assertEquals(bigDecimal, new BigDecimal("68000.00"));
	}
	@Test
	void checkValidUsd(){
		BigDecimal bigDecimal = CurrencyConverter.validUsd(new BigDecimal("15.89189456"));
		assertEquals(new BigDecimal("15.89") , bigDecimal);
		BigDecimal bigDecimal1 = CurrencyConverter.validUsd(new BigDecimal("00.8912364"));
		assertEquals(new BigDecimal("0.89") , bigDecimal1);
		BigDecimal bigDecimal2 = CurrencyConverter.validUsd(new BigDecimal("00.00887456"));
		assertEquals(new BigDecimal("0.0089") , bigDecimal2);
		BigDecimal bigDecimal3 = CurrencyConverter.validUsd(new BigDecimal("0.00000145621"));
		assertEquals(new BigDecimal("0.000001") , bigDecimal3);
		BigDecimal bigDecimal4 = CurrencyConverter.validUsd(new BigDecimal("0.000000014564"));
		assertEquals(new BigDecimal("0.00000001") , bigDecimal4);
		BigDecimal bigDecimal5 = CurrencyConverter.validUsd(new BigDecimal("0.00000000016212"));
		assertEquals(new BigDecimal("0.0000000002") , bigDecimal5);
		BigDecimal bigDecimal6 = CurrencyConverter.validUsd(new BigDecimal("0.000000000001234"));
		assertEquals(new BigDecimal("0.000000000001") , bigDecimal6);
		BigDecimal bigDecimal7 = CurrencyConverter.validUsd(new BigDecimal("0.0000000000000145621"));
		assertEquals(new BigDecimal("0.0000000000000145621") , bigDecimal7);
		BigDecimal bigDecimal8 = CurrencyConverter.validUsd(new BigDecimal("1.8978757458945"));
		assertEquals(new BigDecimal("1.90") , bigDecimal8);
		BigDecimal bigDecimal9 = CurrencyConverter.validUsd(new BigDecimal("150.8987574"));
		assertEquals(new BigDecimal("150.90") , bigDecimal9);
		BigDecimal bigDecimal10 = CurrencyConverter.validUsd(new BigDecimal("15.89074575"));
		assertEquals(new BigDecimal("15.89") , bigDecimal10);
	}


}