package org.example.websocet;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import lombok.RequiredArgsConstructor;
import org.example.xchange.DTO.LimitOrderMain;
import org.example.xchange.DTO.OrderMain;
import org.example.xchange.config.CurrencyProperties;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static info.bitrich.xchangestream.binance.BinanceStreamingExchange.USE_REALTIME_BOOK_TICKER;


@Component
@RequiredArgsConstructor
public class WebSocketBinanceChange implements WebSocketCommand{
	private final CurrencyProperties currencyProperties;
	private final Map<CurrencyPair, StreamingExchange> exchangeMap = new HashMap<>();

	private final BehaviorSubject<LimitOrderMain> subject = BehaviorSubject.create();
	public void addObserver(Observer<LimitOrderMain> observer) {
		subject.subscribe(observer);
	}
	public void removeObserver(Disposable disposable){
		disposable.dispose();
	}
	public boolean hasObservers(){
		return subject.hasObservers();
	}

	@PostConstruct
	public void streamBtcUSDT(){
//		List<String> currencyPairs = currencyProperties.getExchanges().values().stream().filter(exchange -> ChangeType.Binance.toString().equals(exchange.getType()))
//				.flatMap(exchange -> exchange.getPairs().stream()).toList();
//
//		for (String currencyPair : currencyPairs) {
//			createExchangeForCurrencyPair(new CurrencyPair(currencyPair));
//		}

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
					OrderMain orderMain = OrderMain.builder()
							.id(trade.getId())
							.instrument(trade.getInstrument())
							.build();
					LimitOrderMain limitOrderMain = LimitOrderMain.builder()
							.limitPrice(trade.getPrice())
							.orderMain(orderMain)
							.build();
					subject.onNext(limitOrderMain);
				}, subject:: onError);

		exchangeMap.put(currencyPair, exchange);
	}
	@Override
	public Observable<LimitOrderMain> getCurrencyRateStream() {
		return subject;
	}


}
