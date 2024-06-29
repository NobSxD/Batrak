package org.example.xchange;

import org.apache.log4j.Logger;
import org.example.entity.NodeUser;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public abstract class BasicChange implements BasicChangeInterface{
	private static final Logger logger = Logger.getLogger(BasicChange.class);

	protected Exchange exchange;



	public String limitOrder(Order.OrderType orderType, BigDecimal summa, BigDecimal price, CurrencyPair currencyPair) {
		String orderId = "";
		TradeService tradeService = exchange.getTradeService();
		LimitOrder limitOrder =  new LimitOrder(orderType, summa, currencyPair, "", new Date(), price);

		try {
			orderId = tradeService.placeLimitOrder(limitOrder);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "Ордер на покупку не успешно";
		}
		return orderId;
	}

	public String marketOrder( Order.OrderType orderType, BigDecimal summa, CurrencyPair currencyPair) {
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

	public OrderBook orderBooksLimitOrders(Integer countLimitOrders, NodeUser nodeUser) {
		OrderBook orderBook = null;
		try {
			// Получение сервиса для работы с рыночными данными
			MarketDataService marketDataService = exchange.getMarketDataService();

			// Задание параметров запроса

			// Получение стакана ордеров по торговой паре BTC/USD
			Instrument currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
			orderBook = marketDataService.getOrderBook(currencyPair, countLimitOrders);


			// Вывод информации о стакане ордеров
			System.out.println(orderBook.toString());
		} catch (Exception e){
			logger.error(e.getMessage());
		}
		return orderBook;
	}

	public boolean isOrderExecuted(String orderId){
		TradeService orderService = exchange.getTradeService();
		try {
			OpenOrders openOrders = orderService.getOpenOrders();
				for (LimitOrder order : openOrders.getOpenOrders()) {
					if (order.getId().equals(orderId)) {
						logger.info("Order не исполнен: " + orderId);
						return false;
					}
				}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}


}
