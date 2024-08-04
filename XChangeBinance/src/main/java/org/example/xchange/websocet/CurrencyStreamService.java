package org.example.xchange.websocet;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.RequiredArgsConstructor;
import org.example.entity.enams.ChangeType;
import org.example.xchange.config.CurrencyProperties;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static info.bitrich.xchangestream.binance.BinanceStreamingExchange.USE_REALTIME_BOOK_TICKER;


@Service
@RequiredArgsConstructor
public class CurrencyStreamService implements WebSocketCommand{
	private final CurrencyProperties currencyProperties;

	private final PublishSubject<BigDecimal> subject = PublishSubject.create();
	private final Map<CurrencyPair, StreamingExchange> exchangeMap = new HashMap<>();

	@PostConstruct
	public void streamCurrencies() {
		List<String> currencyPairs = currencyProperties.getExchanges().values().stream().filter(exchange -> ChangeType.Binance.toString().equals(exchange.getType()))
				.flatMap(exchange -> exchange.getPairs().stream()).toList();

		for (String currencyPair : currencyPairs) {
			createExchangeForCurrencyPair(new CurrencyPair(currencyPair));
		}
	}

	private void createExchangeForCurrencyPair(CurrencyPair currencyPair) {
		ExchangeSpecification exchangeSpecification = new ExchangeSpecification(BinanceStreamingExchange.class);
		exchangeSpecification.setShouldLoadRemoteMetaData(true);
		exchangeSpecification.setExchangeSpecificParametersItem(USE_REALTIME_BOOK_TICKER, true);

		StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);

		ProductSubscription subscription = ProductSubscription.create()
				.addTrades(currencyPair)
				.addOrderbook(currencyPair)
				.build();

		exchange.connect(subscription).blockingAwait();

		exchange.getStreamingMarketDataService()
				.getTrades(currencyPair)
				.subscribe(trade -> {
					subject.onNext(trade.getPrice());
					System.out.println(trade.getPrice());
				}, subject:: onError);

		exchangeMap.put(currencyPair, exchange);
	}

	public Observable<BigDecimal> getCurrencyRateStream() {
		return subject;
	}
}

