package org.example.xchange;

import org.example.entity.NodeUser;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;

import java.math.BigDecimal;

public interface BasicChangeInterface {
	boolean isOrderExecuted(String orderId);
	OrderBook orderBooksLimitOrders(Integer countLimitOrders, NodeUser nodeUser);
	String marketOrder(Order.OrderType orderType, BigDecimal summa, CurrencyPair currencyPair);
	String limitOrder(Order.OrderType orderType, BigDecimal summa, BigDecimal price, CurrencyPair currencyPair);

}
