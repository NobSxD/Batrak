package org.example.websocet;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.apache.log4j.Logger;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static info.bitrich.xchangestream.binance.BinanceStreamingExchange.USE_REALTIME_BOOK_TICKER;


@Component
public class WebSocketBinanceChange {

	private final Logger logger = Logger.getLogger(WebSocketBinanceChange.class);

	public WebSocketBinanceChange() throws InterruptedException {
		streamBtcUSDT();
	}
	private PublishSubject<BigDecimal> subject = PublishSubject.create();
	public void addObserver(Observer<BigDecimal> observer) {
		subject.subscribe(observer);
	}
	public void removeObserver(Disposable disposable){
		disposable.dispose();
	}
	public boolean hasObservers(){
		return subject.hasObservers();
	}

	public void streamBtcUSDT() throws InterruptedException {
		PublishSubject<BigDecimal> subject = PublishSubject.create();
		final ExchangeSpecification exchangeSpecification = new ExchangeSpecification(BinanceStreamingExchange.class);
		exchangeSpecification.setShouldLoadRemoteMetaData(true);
		exchangeSpecification.setExchangeSpecificParametersItem(USE_REALTIME_BOOK_TICKER, true);
		StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
		ProductSubscription subscription = ProductSubscription.create().addTrades(CurrencyPair.BTC_USDT).addOrderbook(CurrencyPair.BTC_USDT).build();
		exchange.connect(subscription).blockingAwait();

		exchange.getStreamingMarketDataService()
				.getTrades(CurrencyPair.BTC_USDT)
				.subscribe(trade -> {
					subject.onNext(trade.getPrice());
				//	logger.info(trade.toString());
				});
		Thread.sleep(20000);
	}


}
