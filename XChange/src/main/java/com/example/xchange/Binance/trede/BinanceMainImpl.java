package com.example.xchange.Binance.trede;



import org.knowm.xchange.Exchange;
import org.knowm.xchange.binance.dto.trade.*;
import org.knowm.xchange.binance.service.BinanceTradeServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.service.trade.TradeService;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BinanceMainImpl{


	public BinanceMainImpl() {

	}





	public void tradeStart() {

	}


	public void tradeStop() {

	}

	public List<CurrencyPair> pair() {
		Field[] pairs = CurrencyPair.class.getFields();
		List<CurrencyPair> currencyPairs = new ArrayList<>();
		int i = 0;
		for (Field field : pairs) {
			String pairReplace = field.getName().replaceAll("_", "-");
			if (!pairReplace.equals("base") && !pairReplace.equals("counter")) {
				CurrencyPair currencyPair = new CurrencyPair(pairReplace);
				currencyPairs.add(currencyPair);
			}
		}
		return currencyPairs;
	}

	public String limitOrder(Exchange exchange, Order.OrderType orderType, BigDecimal summa, BigDecimal price, CurrencyPair currencyPair){
		String orderId = "";
		TradeService tradeService = exchange.getTradeService();
		LimitOrder buyOrder = new LimitOrder(orderType, summa, currencyPair, "", new Date(), price);
		try {
			 orderId = tradeService.placeLimitOrder(buyOrder);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "Ордер на покупку не успешно";
		}
		return "Ваш ордес создан под id: " + orderId;
	}

	public String marketOrder(Exchange exchange, Order.OrderType orderType, BigDecimal summa, CurrencyPair currencyPair){
		String orderId = "";
		TradeService tradeService = exchange.getTradeService();
		LimitOrder buyOrder = new LimitOrder(orderType, summa, currencyPair, "", new Date(), BigDecimal.ONE);
		try {
			orderId = tradeService.placeLimitOrder(buyOrder);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "Ордер на покупку не успешно";
		}
		return "Ваш ордес создан под id: " + orderId;
	}


	private static void raw(BinanceTradeServiceRaw  tradeService) throws IOException {

		printRawOpenOrders(tradeService);
		Field[] fields = CurrencyPair.class.getFields();

		// place a limit buy order
		BinanceNewOrder order = tradeService.newOrder(CurrencyPair.BTC_USD, OrderSide.SELL, OrderType.LIMIT, TimeInForce.FOK,
													  new BigDecimal(".001"), new BigDecimal("1000.00"),
													  new BigDecimal("21000"), "", new BigDecimal("22000"), 2l,
													  new BigDecimal("1000"), BinanceNewOrder.NewOrderResponseType.RESULT);
		System.out.println("BitstampOrder return value: " + order);

		printRawOpenOrders(tradeService);

		// Cancel the added order
	//	boolean cancelResult = tradeService.cancelOrderAllProducts(order.clientOrderId);
	//	System.out.println("Canceling returned " + cancelResult);

		printRawOpenOrders(tradeService);
	}

	private static void printRawOpenOrders(BinanceTradeServiceRaw tradeService) throws IOException {

		//BinanceOrder[] openOrders = tradeService.getBinanceOpenOrders(CurrencyPair.BTC_USD);
		//System.out.println("Open Orders: " + openOrders.length);
		//for (BinanceOrder order : openOrders) {
	//		System.out.println(order.toString());
	//	}
	}




}
