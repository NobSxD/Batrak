package org.example.observer;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;

public class CurrentConditionsDisplay implements  ObserverPrice, DisplayElement{

	BigDecimal currentPrice;
	Order.OrderType orderType;
	Instrument instrument;


	private Subject weatherData;

	public CurrentConditionsDisplay(Subject weatherData) {
		this.weatherData = weatherData;
		weatherData.registerObserver(this);
	}

	public void undSubscribe(){
		weatherData.removeObserver(this);
	}

	@Override
	public BigDecimal display() {
		return currentPrice;

	}

	@Override
	public void update(BigDecimal currentPrice, Order.OrderType orderType, Instrument instrument) {
		this.currentPrice = currentPrice;
		this.orderType = orderType;
		this.instrument = instrument;
		display();
	}
}
