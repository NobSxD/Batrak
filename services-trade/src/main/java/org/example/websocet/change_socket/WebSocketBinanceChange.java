package org.example.websocet.change_socket;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import io.reactivex.rxjava3.core.Observable;
import lombok.RequiredArgsConstructor;
import org.example.entity.NodeOrder;
import org.example.entity.enams.ChangeType;
import org.example.websocet.WebSocketChange;
import org.example.xchange.config.CurrencyProperties;
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
		init(ChangeType.Binance, currencyProperties, exchangeSpecification);
	}
	
	@Override
	public Observable<NodeOrder> getCurrencyRateStream() {
		return subject;
	}
	
	@Override
	public ChangeType getType() {
		return ChangeType.Binance;
	}
	
}
