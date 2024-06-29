package org.example.finance;

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

	@Test
	void calculateProfitPercentage(){
		BigDecimal expectedDollarAmount = new BigDecimal("0.50");
		BigDecimal profitAmount = FinancialCalculator.calculateProfitPercentage(cash, bayBTC, selBTC);
		assertEquals(expectedDollarAmount, profitAmount);

	}
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
	void calculateDollarAmount(){
		BigDecimal expectedDollarAmount = new BigDecimal("5.00");
		BigDecimal profitAmount = FinancialCalculator.calculateDollarAmount(cash, bayBTC, selBTC);
		assertEquals(expectedDollarAmount, profitAmount);

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
	void calculateNewPrice(){
		BigDecimal bigDecimal = FinancialCalculator.calculateMinPrice(new BigDecimal("68000"), new BigDecimal("10"));
		assertEquals(bigDecimal, new BigDecimal("74800.00000"));
	}


}