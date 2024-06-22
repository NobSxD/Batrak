package org.example.command.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfitCalculatorTest {
	BigDecimal cash = new BigDecimal("1000");
	BigDecimal bayBTC = new BigDecimal("70000.00");
	BigDecimal selBTC = new BigDecimal("70350.00");
	List<LimitOrder> limitOrdersBay;

	List<LimitOrder> limitOrdersSel;
	Instrument btcUsdt;

	@Test
	void calculateProfitPercentage(){
		BigDecimal expectedDollarAmount = new BigDecimal("0.50");
		BigDecimal profitAmount = ProfitCalculator.calculateProfitPercentage(cash, bayBTC, selBTC);
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
		BigDecimal profitAmount = ProfitCalculator.calculateDollarAmount(cash, bayBTC, selBTC);
		assertEquals(expectedDollarAmount, profitAmount);

	}
	@Test
	void converUsdt(){
		BigDecimal priceBTC = new BigDecimal("67773.2");
		BigDecimal cash = new BigDecimal("67.74");
		BigDecimal expectedBTC = new BigDecimal("0.00100");
		BigDecimal usdt = ProfitCalculator.convertCurrency(priceBTC, cash);
		assertEquals(expectedBTC, usdt);
	}
	@Test
	void maxBayAmount(){
		BigDecimal  expectedPrice = new BigDecimal("67800");
		BigDecimal price = ProfitCalculator.maxAmount(limitOrdersBay);
		assertEquals(expectedPrice, price);

		BigDecimal  expectedPrice2 = new BigDecimal("68000");
		limitOrdersBay.add(0,new LimitOrder(Order.OrderType.BID, new BigDecimal("2.04"), btcUsdt, "", null, new BigDecimal("68000")));
		BigDecimal priceOneIndex = ProfitCalculator.maxAmount(limitOrdersBay);
		assertEquals(expectedPrice2, priceOneIndex);

		BigDecimal  expectedPrice3 = new BigDecimal("68000");
		limitOrdersBay.add(1,new LimitOrder(Order.OrderType.BID, new BigDecimal("2.04"), btcUsdt, "", null, new BigDecimal("67900")));
		BigDecimal priceTwoIndex = ProfitCalculator.maxAmount(limitOrdersBay);
		assertEquals(expectedPrice3, priceTwoIndex);
	}
	@Test
	void maxSelAmount(){
		BigDecimal  expectedPrice = new BigDecimal("68700");
		BigDecimal price = ProfitCalculator.maxAmount(limitOrdersSel, new BigDecimal("68400"));
		System.out.println("прайс продажи " + price);
		assertEquals(expectedPrice, price);

		BigDecimal  expectedPrice2 = new BigDecimal("68000");
		limitOrdersSel.add(0,new LimitOrder(Order.OrderType.ASK, new BigDecimal("2.04"), btcUsdt, "", null, new BigDecimal("68000")));
		BigDecimal priceOneIndex = ProfitCalculator.maxAmount(limitOrdersSel);
		assertEquals(expectedPrice2, priceOneIndex);

		BigDecimal  expectedPrice3 = new BigDecimal("68000");
		limitOrdersSel.add(1,new LimitOrder(Order.OrderType.ASK, new BigDecimal("2.04"), btcUsdt, "", null, new BigDecimal("68100")));
		BigDecimal priceTwoIndex = ProfitCalculator.maxAmount(limitOrdersSel);
		assertEquals(expectedPrice3, priceTwoIndex);

	}
	@Test
	void convertInUsdtpoPriceAndSumma(){
		BigDecimal bigDecimal = ProfitCalculator.convertUsdt(new BigDecimal("1"), new BigDecimal("68000"));
		assertEquals(bigDecimal, new BigDecimal("68000.00"));
	}

	@Test
	void calculateNewPrice(){
		BigDecimal bigDecimal = ProfitCalculator.calculateMinPrice(new BigDecimal("68000"), new BigDecimal("10"));
		assertEquals(bigDecimal, new BigDecimal("74800.00000"));
	}

	@Test
	void checkValidUsd(){
		BigDecimal bigDecimal = ProfitCalculator.validUsd(new BigDecimal("15.89189456"));
		assertEquals(new BigDecimal("15.89") , bigDecimal);
		BigDecimal bigDecimal1 = ProfitCalculator.validUsd(new BigDecimal("00.8912364"));
		assertEquals(new BigDecimal("0.89") , bigDecimal1);
		BigDecimal bigDecimal2 = ProfitCalculator.validUsd(new BigDecimal("00.00887456"));
		assertEquals(new BigDecimal("0.0089") , bigDecimal2);
		BigDecimal bigDecimal3 = ProfitCalculator.validUsd(new BigDecimal("0.00000145621"));
		assertEquals(new BigDecimal("0.000001") , bigDecimal3);
		BigDecimal bigDecimal4 = ProfitCalculator.validUsd(new BigDecimal("0.000000014564"));
		assertEquals(new BigDecimal("0.00000001") , bigDecimal4);
		BigDecimal bigDecimal5 = ProfitCalculator.validUsd(new BigDecimal("0.00000000016212"));
		assertEquals(new BigDecimal("0.0000000002") , bigDecimal5);
		BigDecimal bigDecimal6 = ProfitCalculator.validUsd(new BigDecimal("0.000000000001234"));
		assertEquals(new BigDecimal("0.000000000001") , bigDecimal6);
		BigDecimal bigDecimal7 = ProfitCalculator.validUsd(new BigDecimal("0.0000000000000145621"));
		assertEquals(new BigDecimal("0.0000000000000145621") , bigDecimal7);
		BigDecimal bigDecimal8 = ProfitCalculator.validUsd(new BigDecimal("1.8978757458945"));
		assertEquals(new BigDecimal("1.90") , bigDecimal8);
		BigDecimal bigDecimal9 = ProfitCalculator.validUsd(new BigDecimal("150.8987574"));
		assertEquals(new BigDecimal("150.90") , bigDecimal9);
		BigDecimal bigDecimal10 = ProfitCalculator.validUsd(new BigDecimal("15.89074575"));
		assertEquals(new BigDecimal("15.89") , bigDecimal10);
	}

}