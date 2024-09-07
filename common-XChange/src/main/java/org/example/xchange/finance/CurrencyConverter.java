package org.example.xchange.finance;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class CurrencyConverter {

	public static BigDecimal convertCurrency(BigDecimal price, BigDecimal usdt, int scale) {
		RoundingMode roundingMode = RoundingMode.HALF_UP;
		BigDecimal result = usdt.divide(price, scale, roundingMode);
		log.info("usdt={} / price={} равно={}", usdt, price,result);
		return result;

	}

	public static BigDecimal convertUsdt(BigDecimal price, BigDecimal priceCurrency) {
		int scale = 2;
		RoundingMode roundingMode = RoundingMode.HALF_UP;
		BigDecimal result = priceCurrency.multiply(price).setScale(scale, roundingMode);
		log.info("Криптавалюта={} * price={} равно={}", priceCurrency, price, result);
		return result;

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
