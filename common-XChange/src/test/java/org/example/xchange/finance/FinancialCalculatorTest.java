package org.example.xchange.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinancialCalculatorTest {
	List<LimitOrder> limitOrdersBay;

	List<LimitOrder> limitOrdersSel;
	Instrument btcUsdt;

	@BeforeEach
	void newArray(){
		limitOrdersBay = new ArrayList<>();
		limitOrdersSel = new ArrayList<>();
		btcUsdt = new CurrencyPair("BTC-USDT");
		limitOrdersBay.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("0.05"), btcUsdt, "", null, new BigDecimal("68000")));
		limitOrdersBay.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("0.04"), btcUsdt, "", null, new BigDecimal("67900")));
		limitOrdersBay.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("0.03"), btcUsdt, "", null, new BigDecimal("67800")));
		limitOrdersBay.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("1.05"), btcUsdt, "", null, new BigDecimal("67700")));
		limitOrdersBay.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("0.04"), btcUsdt, "", null, new BigDecimal("67600")));
		limitOrdersBay.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("0.06"), btcUsdt, "", null, new BigDecimal("67500")));
		limitOrdersBay.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("0.07"), btcUsdt, "", null, new BigDecimal("67400")));
		limitOrdersBay.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("0.08"), btcUsdt, "", null, new BigDecimal("67300")));
		limitOrdersBay.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("1.05"), btcUsdt, "", null, new BigDecimal("67200")));
		limitOrdersBay.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("0.09"), btcUsdt, "", null, new BigDecimal("67100")));

		limitOrdersSel.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("0.05"), btcUsdt, "", null, new BigDecimal("68000")));
		limitOrdersSel.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("0.04"), btcUsdt, "", null, new BigDecimal("68100")));
		limitOrdersSel.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("0.03"), btcUsdt, "", null, new BigDecimal("68200")));
		limitOrdersSel.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("1.05"), btcUsdt, "", null, new BigDecimal("68300")));
		limitOrdersSel.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("0.04"), btcUsdt, "", null, new BigDecimal("68400")));
		limitOrdersSel.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("0.06"), btcUsdt, "", null, new BigDecimal("68500")));
		limitOrdersSel.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("0.07"), btcUsdt, "", null, new BigDecimal("68600")));
		limitOrdersSel.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("0.08"), btcUsdt, "", null, new BigDecimal("68700")));
		limitOrdersSel.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("1.05"), btcUsdt, "", null, new BigDecimal("68800")));
		limitOrdersSel.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("0.09"), btcUsdt, "", null, new BigDecimal("68900")));

	}

	@Test
	void testIncreaseByPercentage() {
		// Тест 1: Увеличение на 10%
		BigDecimal amount1 = BigDecimal.valueOf(100);
		BigDecimal percentage1 = BigDecimal.valueOf(0.10);
		BigDecimal expected1 = BigDecimal.valueOf(110).setScale(1);
		assertEquals(expected1, FinancialCalculator.increaseByPercentage(amount1, percentage1.doubleValue()));

		// Тест 2: Увеличение на 50%
		BigDecimal amount2 = BigDecimal.valueOf(200);
		BigDecimal percentage2 = BigDecimal.valueOf(0.50);
		BigDecimal expected2 = BigDecimal.valueOf(300).setScale(1);
		assertEquals(expected2, FinancialCalculator.increaseByPercentage(amount2, percentage2.doubleValue()));

		// Тест 3: Увеличение на 0%
		BigDecimal amount3 = BigDecimal.valueOf(150);
		BigDecimal percentage3 = BigDecimal.valueOf(0);
		BigDecimal expected3 = BigDecimal.valueOf(150).setScale(1);
		assertEquals(expected3, FinancialCalculator.increaseByPercentage(amount3, percentage3.doubleValue()));

		// Тест 4: Увеличение на отрицательный процент
		BigDecimal amount4 = BigDecimal.valueOf(100);
		BigDecimal percentage4 = BigDecimal.valueOf(-0.10);
		BigDecimal expected4 = BigDecimal.valueOf(90).setScale(1);
		assertEquals(expected4, FinancialCalculator.increaseByPercentage(amount4, percentage4.doubleValue()));

		// Тест 5: Параметры null
		try {
			FinancialCalculator.increaseByPercentage(null, 10L);
		} catch (IllegalArgumentException e) {
			assertEquals("Amount and percentage must not be null", e.getMessage());
		}

		try {
			FinancialCalculator.increaseByPercentage(BigDecimal.valueOf(100), 0);
		} catch (IllegalArgumentException e) {
			assertEquals("Amount and percentage must not be null", e.getMessage());
		}
	}


}