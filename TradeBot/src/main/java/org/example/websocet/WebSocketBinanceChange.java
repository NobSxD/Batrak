package org.example.websocet;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;


@Component
public class WebSocketBinanceChange implements WebSocketCommand{

	private final BehaviorSubject<BigDecimal> subject = BehaviorSubject.create();
	public void addObserver(Observer<BigDecimal> observer) {
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

		/*final ExchangeSpecification exchangeSpecification = new ExchangeSpecification(BinanceStreamingExchange.class);
		exchangeSpecification.setShouldLoadRemoteMetaData(true);
		exchangeSpecification.setExchangeSpecificParametersItem(USE_REALTIME_BOOK_TICKER, true);

		StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
		ProductSubscription subscription = ProductSubscription.create()
				.addTrades(CurrencyPair.BTC_USDT)
				.addOrderbook(CurrencyPair.BTC_USDT)
				.build();
		exchange.connect(subscription).blockingAwait();

		exchange.getStreamingMarketDataService()
				.getTrades(CurrencyPair.BTC_USDT)
				.subscribe(trade -> {
					subject.onNext(trade.getPrice());
				}, subject :: onError);

		 */
	}
	@Override
	public Observable<BigDecimal> getCurrencyRateStream() {
		return subject;
	}


}
