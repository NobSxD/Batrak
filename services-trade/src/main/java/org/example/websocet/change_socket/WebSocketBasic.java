package org.example.websocet.change_socket;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import lombok.extern.slf4j.Slf4j;
import org.example.configuration.CurrencyProperties;
import org.example.entity.NodeOrder;
import org.example.entity.collect.Pair;
import org.example.entity.enams.menu.MenuChange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static info.bitrich.xchangestream.binance.BinanceStreamingExchange.USE_REALTIME_BOOK_TICKER;

@Slf4j
public abstract class WebSocketBasic {
	
	protected final BehaviorSubject<NodeOrder> subject = BehaviorSubject.create();
	protected final Map<CurrencyPair, StreamingExchange> exchangeMap = new HashMap<>();
	
	public abstract void streamValue();
	
	protected void init(MenuChange menuChange, CurrencyProperties currencyProperties, ExchangeSpecification exchangeSpecification) {
		List<Pair> currencyPairs = currencyProperties.getExchanges().values().stream().filter(exchange -> menuChange.toString().equals(exchange.getType()))
													 .flatMap(exchange -> exchange.getPairs().stream()).toList();
		for (Pair currencyPair : currencyPairs) {
			createExchangeForCurrencyPair(new CurrencyPair(currencyPair.getNamePair()), exchangeSpecification);
		}
		
	}
	
	private void createExchangeForCurrencyPair(CurrencyPair currencyPair, ExchangeSpecification exchangeSpecification) {
		exchangeSpecification.setShouldLoadRemoteMetaData(true);
		exchangeSpecification.setExchangeSpecificParametersItem(USE_REALTIME_BOOK_TICKER, true);
		StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);

		Observable<NodeOrder> tradeObservable = exchange.getStreamingMarketDataService()
				.getTrades((Instrument) currencyPair)
				.map(trade -> NodeOrder.builder()
						.orderId(trade.getId())
						.instrument(trade.getInstrument().toString())
						.originalAmount(trade.getOriginalAmount())
						.limitPrice(trade.getPrice())
						.timestamp(trade.getTimestamp())
						.type(trade.getType().toString())
						.build())
				.doOnError(error -> {
					log.error("Ошибка при получении данных: ", error);
				});
		tradeObservable
				.retryWhen(errors -> errors
						.doOnNext(error -> log.warn("Переподключение из-за ошибки: ", error))
						.flatMap(error -> {
							log.info("Попытка переподключения через 5 секунд...");
							return Observable.timer(5, TimeUnit.SECONDS);
						})
				)
				.subscribe(
						subject::onNext,
						subject::onError
				);
		exchangeMap.put(currencyPair, exchange);
	}
	
}
