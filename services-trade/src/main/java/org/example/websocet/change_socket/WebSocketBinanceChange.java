package org.example.websocet.change_socket;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import io.reactivex.rxjava3.core.Observable;
import org.example.configuration.CurrencyProperties;
import org.example.websocet.WebSocketChange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.instrument.Instrument;

import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

import java.math.BigDecimal;

import org.example.entity.collect.ChangeType;

import org.springframework.stereotype.Component;

import static info.bitrich.xchangestream.binance.BinanceStreamingExchange.USE_REALTIME_BOOK_TICKER;

@Component
@RequiredArgsConstructor
public class WebSocketBinanceChange extends WebSocketBasic implements WebSocketChange {
	
	private final CurrencyProperties currencyProperties;
	
	@PostConstruct
	public void streamValue() {
		ExchangeSpecification exchangeSpecification = new ExchangeSpecification(BinanceStreamingExchange.class);
		exchangeSpecification.setShouldLoadRemoteMetaData(true);
		exchangeSpecification.setExchangeSpecificParametersItem(USE_REALTIME_BOOK_TICKER, true);
		init(ChangeType.Binance, currencyProperties, exchangeSpecification);
	}

	@Override
	public Observable<BigDecimal> exchangePair(Instrument instrument){
		return exchangeMap.get(instrument);
	}
	
	@Override
	public ChangeType getType() {
		return ChangeType.Binance;
	}
	
}
