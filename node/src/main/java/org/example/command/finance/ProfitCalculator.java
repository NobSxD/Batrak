package org.example.command.finance;

import org.apache.log4j.Logger;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ProfitCalculator {
	private final static Logger logger = Logger.getLogger(ProfitCalculator.class);
	public static BigDecimal calculateProfitPercentage(BigDecimal cash, BigDecimal bayBTC, BigDecimal selBTC) {
		BigDecimal initialBTC = cash.divide(bayBTC, 8, RoundingMode.HALF_UP);
		BigDecimal finalValue = initialBTC.multiply(selBTC);
		BigDecimal profitAmount = finalValue.subtract(cash);
		BigDecimal b = profitAmount.divide(cash).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
		return b;
	}

	public static BigDecimal calculateDollarAmount(BigDecimal cash, BigDecimal bayBTC, BigDecimal selBTC) {
		BigDecimal initialBTC = cash.divide(bayBTC, 8, RoundingMode.HALF_UP);
		BigDecimal finalValue = initialBTC.multiply(selBTC);
		return finalValue.subtract(cash).setScale(2, RoundingMode.HALF_UP);
	}

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

	public static BigDecimal maxAmount(List<LimitOrder> limitOrders) {
		int index = 0;
		BigDecimal maxOriginalAmount = BigDecimal.ZERO;
		LimitOrder orderWithMaxOriginalAmount = null;
		for (int i = 0; i < limitOrders.size(); i++) {
			if (limitOrders.get(i).getOriginalAmount().compareTo(maxOriginalAmount) > 0) {
				maxOriginalAmount = limitOrders.get(i).getOriginalAmount();
				orderWithMaxOriginalAmount = limitOrders.get(i);
				index = i;
			}
		}
		BigDecimal max = null;
		if (orderWithMaxOriginalAmount != null) {
			logger.info("Наибольшее originalAmount: " + maxOriginalAmount);
			logger.info("Цена: " + orderWithMaxOriginalAmount.getLimitPrice());
			if (index > 0) {
				max = limitOrders.get(index - 1).getLimitPrice();
			} else {
				max = limitOrders.get(index).getLimitPrice();
			}
		} else {
			max = limitOrders.get(0).getLimitPrice();
		}

		logger.info("Цена ордера: " + max);
		return max;
	}

	public static BigDecimal validUsd(BigDecimal usd) {
		if (usd.compareTo(BigDecimal.ONE) > 0) {
			return usd.setScale(2, RoundingMode.HALF_UP);
		} else if (usd.compareTo(new BigDecimal("0.99")) < 0 && usd.compareTo(new BigDecimal("0.01")) > 0) {
			return usd.setScale(2, RoundingMode.HALF_UP);
		} else if (usd.compareTo(new BigDecimal("0.01")) < 0 && usd.compareTo(new BigDecimal("0.0001")) > 0) {
			return usd.setScale(4, RoundingMode.HALF_UP);
		} else if (usd.compareTo(new BigDecimal("0.0001")) < 0 && usd.compareTo(new BigDecimal("0.000001")) > 0) {
			return usd.setScale(6, RoundingMode.HALF_UP);
		} else if (usd.compareTo(new BigDecimal("0.000001")) < 0 && usd.compareTo(new BigDecimal("0.00000001")) > 0) {
			return usd.setScale(8, RoundingMode.HALF_UP);
		} else if (usd.compareTo(new BigDecimal("0.00000001")) < 0 && usd.compareTo(new BigDecimal("0.0000000001")) > 0) {
			return usd.setScale(10, RoundingMode.HALF_UP);
		} else if (usd.compareTo(new BigDecimal("0.0000000001")) < 0 && usd.compareTo(new BigDecimal("0.000000000001")) > 0) {
			return usd.setScale(12, RoundingMode.HALF_UP);
		}

		return usd;
	}

	public static BigDecimal maxAmount(List<LimitOrder> limitOrders, BigDecimal minPrice) {
		int index = 0;
		BigDecimal maxOriginalAmount = BigDecimal.ZERO;
		LimitOrder orderWithMaxOriginalAmount = null;
		for (int i = 0; i < limitOrders.size(); i++) {
			if (limitOrders.get(i).getLimitPrice().compareTo(minPrice) > 0 && limitOrders.get(i).getOriginalAmount().compareTo(maxOriginalAmount) > 0) {
				maxOriginalAmount = limitOrders.get(i).getOriginalAmount();
				orderWithMaxOriginalAmount = limitOrders.get(i);
				index = i;
			}
		}

		if (orderWithMaxOriginalAmount != null) {
			logger.info("Наибольшее originalAmount: " + maxOriginalAmount);
			logger.info("Цена: " + orderWithMaxOriginalAmount.getLimitPrice());
			if (index > 0) {
				return limitOrders.get(index - 1).getLimitPrice();
			}
			return limitOrders.get(index).getLimitPrice();
		}
		logger.info("Цена ордера: " + minPrice);
		return minPrice;
	}


	public static BigDecimal calculateMinPrice(BigDecimal price, BigDecimal spread) {
		// Увеличиваем price на значение spread
		spread = spread.divide(new BigDecimal("100"), 5, RoundingMode.HALF_UP);
		BigDecimal increaseAmount = price.multiply(spread);
		BigDecimal newPrice = price.add(increaseAmount);

		return newPrice;
	}
}
