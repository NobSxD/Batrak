package org.example.xchange;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.service.trade.TradeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public abstract class BasicChange {
	private Exchange exchange;


	public String limitOrder(Order.OrderType orderType, BigDecimal summa, BigDecimal price, CurrencyPair currencyPair) {
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

	public String marketOrder(Order.OrderType orderType, BigDecimal summa, CurrencyPair currencyPair) {
		String orderId = "";
		TradeService tradeService = exchange.getTradeService();
		MarketOrder marketOrder = new MarketOrder(orderType, summa, currencyPair, null, new Date());
		try {
			orderId = tradeService.placeMarketOrder(marketOrder);
			System.out.println(orderId);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "Ордер на покупку не успешно";
		}
		return "Ваш ордес создан под id: " + orderId;
	}

}
