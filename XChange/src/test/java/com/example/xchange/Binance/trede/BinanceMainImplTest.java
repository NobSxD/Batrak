package com.example.xchange.Binance.trede;


import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class BinanceMainImplTest {


	void pair() {
		BinanceMainImpl binanceMain = new BinanceMainImpl();

		Field[] pair = binanceMain.pair();
		List<CurrencyPair> currencyPairs = new ArrayList<>();
		int i = 0;
		for (Field field : pair) {
			String pairReplace = field.getName().replaceAll("_", "-");
			if (!pairReplace.equals("base") && !pairReplace.equals("counter")) {
				CurrencyPair currencyPair = new CurrencyPair(pairReplace);
				currencyPairs.add(currencyPair);
			}
			System.out.println(i++);

		}
        currencyPairs.isEmpty();
	}

	public static void main(String[] args) {
		Exchange exchange = BinanceDemoUtils.createExchange();
		TradeService tradeService = exchange.getTradeService();
		generic(tradeService);

	}
	private static void generic(TradeService tradeService){
		OpenOrdersParams openOrdersParams = tradeService.createOpenOrdersParams();

	}
}