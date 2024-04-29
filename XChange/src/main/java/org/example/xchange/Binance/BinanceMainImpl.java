package org.example.xchange.Binance;


import lombok.Getter;
import lombok.Setter;
import org.example.entity.NodeUser;
import org.example.xchange.BasicChange;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.trade.BinanceNewOrder;
import org.knowm.xchange.binance.dto.trade.OrderSide;
import org.knowm.xchange.binance.dto.trade.OrderType;
import org.knowm.xchange.binance.dto.trade.TimeInForce;
import org.knowm.xchange.binance.service.BinanceTradeServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class BinanceMainImpl extends BasicChange{
	private Exchange exchange;

	public BinanceMainImpl(NodeUser nodeUser) {
		ExchangeSpecification exSpec = new BinanceExchange().getDefaultExchangeSpecification();
		exSpec.setUserName(nodeUser.getAccount().getNameChange());
		exSpec.setApiKey(nodeUser.getAccount().getPublicApiKey());
		exSpec.setSecretKey(nodeUser.getAccount().getSecretApiKey());
		this.exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
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
