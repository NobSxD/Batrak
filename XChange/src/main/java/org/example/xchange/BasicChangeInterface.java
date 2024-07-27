package org.example.xchange;

import org.example.xchange.DTO.LimitOrderMain;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;
import java.util.List;

public interface BasicChangeInterface {

	OrderBook orderBooksLimitOrders(Integer countLimitOrders, String pairName);
	String marketOrder(Order.OrderType orderType, BigDecimal summa, CurrencyPair currencyPair);
	String limitOrder(Order.OrderType orderType, BigDecimal summa, BigDecimal price, CurrencyPair currencyPair);
	void cancelOrder(String namePair, String idOrder);
	String accountInfo();

	LimitOrder createOrder(Instrument currencyPair, List<BigDecimal> priceAndAmount, Order.OrderType orderType);
	List<LimitOrder> createOrders(Instrument currencyPair, Order.OrderType orderType, List<List<BigDecimal>> orders);
	LimitOrderMain placeLimitOrder(LimitOrder limitOrder, boolean trade);

}
