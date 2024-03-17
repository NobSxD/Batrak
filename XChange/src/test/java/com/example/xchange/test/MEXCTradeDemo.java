package com.example.xchange.test;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.mexc.service.MEXCTradeServiceRaw;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;

import java.io.IOException;
import java.math.BigDecimal;

public class MEXCTradeDemo {
	public static void main(String[] args) throws IOException {

		Exchange exchange = MEXCDemoUtils.createExchange();
		AccountService accountService = exchange.getAccountService();
		AccountInfo accountInfo = accountService.getAccountInfo();
		System.out.println(accountInfo.toString());

	//	generic(tradeService);
	//	raw((MEXCTradeServiceRaw) tradeService);
	}

	private static void generic(TradeService tradeService) throws IOException {
	//	LimitOrder limitOrder = new LimitOrder(Order.OrderType.ASK,);
	//	String placeLimitOrder = tradeService.placeLimitOrder();
		final OpenOrdersParamCurrencyPair openOrdersParamsBtcEur = (OpenOrdersParamCurrencyPair) tradeService.createOpenOrdersParams();
		openOrdersParamsBtcEur.setCurrencyPair(CurrencyPair.ADA_BNB);
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

	private static void raw(MEXCTradeServiceRaw tradeService) throws IOException {


	}


}
