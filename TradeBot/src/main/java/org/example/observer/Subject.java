package org.example.observer;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;

public interface Subject {
	void registerObserver(ObserverPrice observerPrice);
	void removeObserver(ObserverPrice observerPrice);
	void notifyObservers();
	void setPrice(BigDecimal price, Order.OrderType orderType, Instrument instrument);
}
