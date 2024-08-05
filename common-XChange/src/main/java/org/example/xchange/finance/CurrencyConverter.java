package org.example.xchange.finance;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConverter {

	public static BigDecimal convertCurrency(BigDecimal originalAmount, BigDecimal usdt) {
		int scale = 5;
		RoundingMode roundingMode = RoundingMode.HALF_UP;
		return usdt.divide(originalAmount, scale, roundingMode);

	}

	public static BigDecimal convertUsdt(BigDecimal originalAmount, BigDecimal priceCurrency) {
		int scale = 2;
		RoundingMode roundingMode = RoundingMode.HALF_UP;
		return priceCurrency.multiply(originalAmount).setScale(scale, roundingMode);

	}

	public static BigDecimal validUsd(BigDecimal usd) {
		 if (usd.compareTo(new BigDecimal("0.01")) > 0) {
			return usd.setScale(2, RoundingMode.HALF_UP);
		}
		else if (usd.compareTo(new BigDecimal("0.01")) < 0 && usd.compareTo(new BigDecimal("0.0001")) > 0) {
			return usd.setScale(4, RoundingMode.HALF_UP);
		}
		else if (usd.compareTo(new BigDecimal("0.0001")) < 0 && usd.compareTo(new BigDecimal("0.000001")) > 0) {
			return usd.setScale(6, RoundingMode.HALF_UP);
		}
		else if (usd.compareTo(new BigDecimal("0.000001")) < 0 && usd.compareTo(new BigDecimal("0.00000001")) > 0) {
			return usd.setScale(8, RoundingMode.HALF_UP);
		}
		else if (usd.compareTo(new BigDecimal("0.00000001")) < 0 && usd.compareTo(new BigDecimal("0.0000000001")) > 0) {
			return usd.setScale(10, RoundingMode.HALF_UP);
		}
		else if (usd.compareTo(new BigDecimal("0.0000000001")) < 0 && usd.compareTo(new BigDecimal("0.000000000001")) > 0) {
			return usd.setScale(12, RoundingMode.HALF_UP);
		}

		return usd;
	}

}
