package org.example.observer;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;

public interface ObserverPrice {
	void update(BigDecimal currentPrice, Order.OrderType orderType, Instrument instrument);
}
