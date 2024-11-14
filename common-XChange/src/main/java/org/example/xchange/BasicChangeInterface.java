package org.example.xchange;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;
import java.util.List;

public interface BasicChangeInterface {
	OrderBook orderBooksLimitOrders(Integer countLimitOrders, String pairName);
	void cancelOrder(String namePair, String idOrder);
	String accountInfo();

	LimitOrder createOrder(Instrument currencyPair, List<BigDecimal> priceAndAmount, Order.OrderType orderType);
	MarketOrder createMarketOrder(Order.OrderType orderType, BigDecimal coinAmount, BigDecimal price,
								  int scale, Instrument currencyPair);
	List<LimitOrder> createOrders(Instrument currencyPair, Order.OrderType orderType, List<List<BigDecimal>> orders);
	String placeLimitOrder(LimitOrder limitOrder);
	String placeMarketOrder(MarketOrder marketOrder);

}
