package org.example.websocet.change_socket;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import lombok.RequiredArgsConstructor;
import org.example.entity.NodeOrder;
import org.example.entity.enams.ChangeType;
import org.example.websocet.WebSocketChange;
import org.example.xchange.config.CurrencyProperties;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static info.bitrich.xchangestream.binance.BinanceStreamingExchange.USE_REALTIME_BOOK_TICKER;

@Component
@RequiredArgsConstructor
public class WebSocketBinanceChange implements WebSocketChange {
	
	private final CurrencyProperties currencyProperties;
	private final Map<CurrencyPair, StreamingExchange> exchangeMap = new HashMap<>();
	
	private final BehaviorSubject<NodeOrder> subject = BehaviorSubject.create();
	
	@PostConstruct
	public void streamBtcUSDT() {
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
					NodeOrder nodeOrder = NodeOrder.builder()
												   .orderId(trade.getId())
												   .instrument(trade.getInstrument().toString())
												   .originalAmount(trade.getOriginalAmount())
												   .limitPrice(trade.getPrice())
												   .timestamp(trade.getTimestamp())
												   .type(trade.getType().toString())
												   .build();
					subject.onNext(nodeOrder);
				}, subject :: onError);
		
		exchangeMap.put(currencyPair, exchange);
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
