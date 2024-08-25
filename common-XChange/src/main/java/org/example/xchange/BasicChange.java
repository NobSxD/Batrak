package org.example.xchange;

import org.example.xchange.finance.CurrencyConverter;

import lombok.ToString;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.knowm.xchange.service.trade.params.DefaultCancelOrderByInstrumentAndIdParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@ToString
public abstract class BasicChange implements BasicChangeInterface, Serializable {
	private static final Logger logger = LoggerFactory.getLogger(BasicChange.class);

	protected Exchange exchange;


	public String limitOrder(Order.OrderType orderType, BigDecimal summa, BigDecimal price, Instrument currencyPair) {
		BigDecimal usdt = CurrencyConverter.convertCurrency(price, summa);
		String orderId = "";
		TradeService tradeService = exchange.getTradeService();
		LimitOrder limitOrder = new LimitOrder(orderType, usdt, currencyPair, "", new Date(), price);

		try {
			orderId = tradeService.placeLimitOrder(limitOrder);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return "Ордер на покупку не успешно";
		}
		return orderId;
	}

	public String placeLimitOrder(LimitOrder limitOrder, boolean trade) { //если false то мы не отпровляем ордер на биржу
		try {
			TradeService tradeService = exchange.getTradeService();
			String orderId = "";
			if (trade) {
				orderId = tradeService.placeLimitOrder(limitOrder);
			}
			logger.info(limitOrder.toString());
			return orderId;
		} catch (IOException e) {
			logger.error(e.getMessage() + " " + limitOrder.toString());
			throw new RuntimeException(e);
		}
	}

	public LimitOrder createOrder(Instrument currencyPair, List<BigDecimal> priceAndAmount, Order.OrderType orderType) {
		return new LimitOrder(orderType, priceAndAmount.get(1), currencyPair, "", null, priceAndAmount.get(0));
	}

	public List<LimitOrder> createOrders(Instrument currencyPair, Order.OrderType orderType, List<List<BigDecimal>> orders) {
		List<LimitOrder> limitOrders = new ArrayList<>();
		for (List<BigDecimal> ask : orders) {
			checkArgument(
					ask.size() == 2, "Expected a pair (price, amount) but got {0} elements.", ask.size());
			limitOrders.add(createOrder(currencyPair, ask, orderType));
		}
		return limitOrders;
	}

	public void checkArgument(boolean argument, String msgPattern, Object... msgArgs) {
		if (! argument) {
			throw new IllegalArgumentException(MessageFormat.format(msgPattern, msgArgs));
		}
	}

	public String marketOrder(Order.OrderType orderType, BigDecimal summa, Instrument currencyPair) {
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

	public OrderBook orderBooksLimitOrders(Integer countLimitOrders, String pairName) {
		OrderBook orderBook = null;
		try {
			// Получение сервиса для работы с рыночными данными
			MarketDataService marketDataService = exchange.getMarketDataService();
			// Задание параметров запроса

			// Получение стакана ордеров по торговой паре BTC/USD
			Instrument currencyPair = new CurrencyPair(pairName);
			orderBook = marketDataService.getOrderBook(currencyPair, countLimitOrders);

			// Вывод информации о стакане ордеров
			System.out.println(orderBook.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return orderBook;
	}


	public void cancelOrder(String namePair, String idOrder) {
		try {
			Instrument instrument = new CurrencyPair(namePair);
			CancelOrderParams cancelOrderParams = new DefaultCancelOrderByInstrumentAndIdParams(instrument, idOrder);
			exchange.getTradeService().cancelOrder(cancelOrderParams);
		} catch (IOException e) {
			logger.error(e.getMessage() + " ордер id: " + idOrder);
			throw new RuntimeException(e);
		}

	}

	public String accountInfo() {
		try {
			AccountInfo accountInfo = exchange.getAccountService().getAccountInfo();
			return accountInfo.toString();
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public static LocalDateTime convertDateToLocalDateTime(Date date) {
		return Instant.ofEpochMilli(date.getTime())
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}
}