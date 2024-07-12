package org.example.observer;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;
import java.util.ArrayList;

public abstract class StockData implements Subject{
	protected ArrayList<ObserverPrice> observerPrices;
	protected BigDecimal currentPrice;
	protected Order.OrderType orderType;
	protected Instrument instrument;

	public StockData(){
		observerPrices = new ArrayList<>();
	}
	@Override
	public void registerObserver(ObserverPrice observerPrice) {
		observerPrices.add(observerPrice);
	}

	@Override
	public void removeObserver(ObserverPrice observerPrice) {
		int i = observerPrices.indexOf(observerPrice);
		if (i >= 0){
			observerPrices.remove(i);
		}
	}

	@Override
	public void notifyObservers() {
		for (int i = 0; i < observerPrices.size(); i++) {
			ObserverPrice observerPrice =  observerPrices.get(i);
			observerPrice.update(currentPrice, orderType, instrument);
		}
	}
	public void measurementsChanged(){
		notifyObservers();
	}

	public void setPrice(BigDecimal price, Order.OrderType orderType, Instrument instrument){
		this.currentPrice = price;
		this.orderType = orderType;
		this.instrument = instrument;
		measurementsChanged();
	}
}
