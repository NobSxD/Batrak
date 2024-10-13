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
	BigDecimal cash = new BigDecimal("1000");
	BigDecimal bayBTC = new BigDecimal("70000.00");
	BigDecimal selBTC = new BigDecimal("70350.00");
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
	void maxBayAmount(){
		BigDecimal  expectedPrice = new BigDecimal("67800");
		BigDecimal price = FinancialCalculator.maxAmount(limitOrdersBay);
		assertEquals(expectedPrice, price);

		BigDecimal  expectedPrice2 = new BigDecimal("68000");
		limitOrdersBay.add(0,new LimitOrder(Order.OrderType.BID, new BigDecimal("2.04"), btcUsdt, "", null, new BigDecimal("68000")));
		BigDecimal priceOneIndex = FinancialCalculator.maxAmount(limitOrdersBay);
		assertEquals(expectedPrice2, priceOneIndex);

		BigDecimal  expectedPrice3 = new BigDecimal("68000");
		limitOrdersBay.add(1,new LimitOrder(Order.OrderType.BID, new BigDecimal("2.04"), btcUsdt, "", null, new BigDecimal("67900")));
		BigDecimal priceTwoIndex = FinancialCalculator.maxAmount(limitOrdersBay);
		assertEquals(expectedPrice3, priceTwoIndex);
	}
	@Test
	void maxSelAmount(){
		BigDecimal  expectedPrice = new BigDecimal("68700");
		BigDecimal price = FinancialCalculator.maxAmount(limitOrdersSel, new BigDecimal("68400"));
		System.out.println("прайс продажи " + price);
		assertEquals(expectedPrice, price);

		BigDecimal  expectedPrice2 = new BigDecimal("68000");
		limitOrdersSel.add(0,new LimitOrder(Order.OrderType.ASK, new BigDecimal("2.04"), btcUsdt, "", null, new BigDecimal("68000")));
		BigDecimal priceOneIndex = FinancialCalculator.maxAmount(limitOrdersSel);
		assertEquals(expectedPrice2, priceOneIndex);

		BigDecimal  expectedPrice3 = new BigDecimal("68000");
		limitOrdersSel.add(1,new LimitOrder(Order.OrderType.ASK, new BigDecimal("2.04"), btcUsdt, "", null, new BigDecimal("68100")));
		BigDecimal priceTwoIndex = FinancialCalculator.maxAmount(limitOrdersSel);
		assertEquals(expectedPrice3, priceTwoIndex);

	}
	@Test
	void gridList(){
		BigDecimal initialRate = new BigDecimal("1000");        // Начальная цена
		BigDecimal percentageStep = new BigDecimal("5");        // Шаг в процентах
		int size = 5;                                           // Размер списка

		List<BigDecimal> bigDecimals = FinancialCalculator.gridLevels(initialRate, percentageStep, size, 5);
		System.out.println("Сетка уровней ордеров: " + bigDecimals);
	}

	@Test
	void sizeGridLevels(){
		int i = FinancialCalculator.sizeGridLevels(new BigDecimal("1000"), new BigDecimal("100"));
		assertEquals(i, 10);
		int i2 = FinancialCalculator.sizeGridLevels(new BigDecimal("1000"), new BigDecimal("11"));
		assertEquals(i2, 90);
		int i3 = FinancialCalculator.sizeGridLevels(new BigDecimal("87945"), new BigDecimal("123"));
		assertEquals(i3, 715);
		int i4 = FinancialCalculator.sizeGridLevels(new BigDecimal("3213"), new BigDecimal("147"));
		assertEquals(i4, 21);
		int i5 = FinancialCalculator.sizeGridLevels(new BigDecimal("10"), new BigDecimal("100"));
		assertEquals(i5, 0);
	}

	@Test
	void stepPrice(){
		BigDecimal bigDecimal = FinancialCalculator.stepPrice(new BigDecimal("60000"), new BigDecimal("10"));
		assertEquals(bigDecimal, new BigDecimal("66000.00"));

		BigDecimal bigDecimal2 = FinancialCalculator.stepPrice(new BigDecimal("58963"), new BigDecimal("6.5"));
		assertEquals(bigDecimal2, new BigDecimal("62795.60"));

		BigDecimal bigDecimal3 = FinancialCalculator.stepPrice(new BigDecimal("0.00569"), new BigDecimal("6.5"));
		assertEquals(bigDecimal3, new BigDecimal("0.0061"));

		BigDecimal bigDecimal4 = FinancialCalculator.stepPrice(new BigDecimal("0.0170"), new BigDecimal("10"));
		assertEquals(bigDecimal4, new BigDecimal("0.02"));

	}

	@Test
	void testIncreaseByPercentage() {
		// Тест 1: Увеличение на 10%
		BigDecimal amount1 = BigDecimal.valueOf(100);
		BigDecimal percentage1 = BigDecimal.valueOf(0.10);
		BigDecimal expected1 = BigDecimal.valueOf(110).setScale(2);
		assertEquals(expected1, FinancialCalculator.increaseByPercentage(amount1, percentage1));

		// Тест 2: Увеличение на 50%
		BigDecimal amount2 = BigDecimal.valueOf(200);
		BigDecimal percentage2 = BigDecimal.valueOf(0.50);
		BigDecimal expected2 = BigDecimal.valueOf(300).setScale(2);
		assertEquals(expected2, FinancialCalculator.increaseByPercentage(amount2, percentage2));

		// Тест 3: Увеличение на 0%
		BigDecimal amount3 = BigDecimal.valueOf(150);
		BigDecimal percentage3 = BigDecimal.valueOf(0);
		BigDecimal expected3 = BigDecimal.valueOf(150).setScale(2);
		assertEquals(expected3, FinancialCalculator.increaseByPercentage(amount3, percentage3));

		// Тест 4: Увеличение на отрицательный процент
		BigDecimal amount4 = BigDecimal.valueOf(100);
		BigDecimal percentage4 = BigDecimal.valueOf(-0.10);
		BigDecimal expected4 = BigDecimal.valueOf(90).setScale(2);
		assertEquals(expected4, FinancialCalculator.increaseByPercentage(amount4, percentage4));

		// Тест 5: Параметры null
		try {
			FinancialCalculator.increaseByPercentage(null, BigDecimal.valueOf(10));
		} catch (IllegalArgumentException e) {
			assertEquals("Amount and percentage must not be null", e.getMessage());
		}

		try {
			FinancialCalculator.increaseByPercentage(BigDecimal.valueOf(100), null);
		} catch (IllegalArgumentException e) {
			assertEquals("Amount and percentage must not be null", e.getMessage());
		}
	}


}