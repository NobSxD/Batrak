package com.example.strategy;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;

import java.io.IOException;
import java.math.BigDecimal;

public class Strategy {
	private static void generic(TradeService tradeService) throws IOException {
		final OpenOrdersParamCurrencyPair openOrdersParamsBtcEur = (OpenOrdersParamCurrencyPair) tradeService.createOpenOrdersParams();
		openOrdersParamsBtcEur.setCurrencyPair(CurrencyPair.BTC_EUR);
		final OpenOrdersParamCurrencyPair openOrdersParamsBtcUsd = (OpenOrdersParamCurrencyPair) tradeService.createOpenOrdersParams();
		openOrdersParamsBtcUsd.setCurrencyPair(CurrencyPair.BTC_USD);

		final OpenOrdersParams openOrdersParamsAll = tradeService.createOpenOrdersParams();

		printOpenOrders(tradeService, openOrdersParamsAll);

		// place a limit buy order
		LimitOrder limitOrder = new LimitOrder((Order.OrderType.BID), new BigDecimal(".01"), CurrencyPair.BTC_EUR, null, null, new BigDecimal("500.00"));
		String limitOrderReturnValue = tradeService.placeLimitOrder(limitOrder);
		System.out.println("Limit Order return value: " + limitOrderReturnValue);

		printOpenOrders(tradeService, openOrdersParamsAll);
		printOpenOrders(tradeService, openOrdersParamsBtcEur);
		printOpenOrders(tradeService, openOrdersParamsBtcUsd);

		// Cancel the added order
		boolean cancelResult = tradeService.cancelOrder(limitOrderReturnValue);
		System.out.println("Canceling returned " + cancelResult);

		printOpenOrders(tradeService, openOrdersParamsAll);
	}

	private static void printOpenOrders(TradeService tradeService, OpenOrdersParams openOrdersParams) throws IOException {
		OpenOrders openOrders = tradeService.getOpenOrders(openOrdersParams);
		System.out.printf("Open Orders for %s: %s%n", openOrdersParams, openOrders);
	}


}
