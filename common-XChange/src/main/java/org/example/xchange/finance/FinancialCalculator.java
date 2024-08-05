package org.example.xchange.finance;

import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class FinancialCalculator {
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
			if (index > 0) {
				max = limitOrders.get(index - 1).getLimitPrice();
			} else {
				max = limitOrders.get(index).getLimitPrice();
			}
		} else {
			max = limitOrders.get(0).getLimitPrice();
		}
		return max;
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
			if (index > 0) {
				return limitOrders.get(index - 1).getLimitPrice();
			}
			return limitOrders.get(index).getLimitPrice();
		}
		return minPrice;
	}


	public static BigDecimal calculateMinPrice(BigDecimal price, BigDecimal spread) {
		spread = spread.divide(new BigDecimal("100"), 5, RoundingMode.HALF_UP);
		BigDecimal increaseAmount = price.multiply(spread);
		BigDecimal newPrice = price.add(increaseAmount);

		return newPrice;
	}
}
