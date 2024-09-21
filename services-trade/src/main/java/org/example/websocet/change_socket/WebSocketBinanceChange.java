package org.example.websocet.change_socket;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import io.reactivex.rxjava3.core.Observable;
import lombok.RequiredArgsConstructor;
import org.example.configuration.CurrencyProperties;
import org.example.entity.NodeOrder;
import org.example.entity.enams.menu.MenuChange;
import org.example.websocet.WebSocketChange;
import org.knowm.xchange.ExchangeSpecification;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class WebSocketBinanceChange extends WebSocketBasic implements WebSocketChange {
	
	private final CurrencyProperties currencyProperties;
	
	@PostConstruct
	public void streamValue() {
		ExchangeSpecification exchangeSpecification = new ExchangeSpecification(BinanceStreamingExchange.class);
		init(MenuChange.Binance, currencyProperties, exchangeSpecification);
	}
	
	@Override
	public Observable<NodeOrder> getCurrencyRateStream() {
		return subject;
	}
	
	@Override
	public MenuChange getType() {
		return MenuChange.Binance;
	}
	
}
