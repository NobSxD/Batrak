package org.example.websocet.change_socket;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.NodeOrder;
import org.example.entity.enams.ChangeType;
import org.example.xchange.config.CurrencyProperties;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static info.bitrich.xchangestream.binance.BinanceStreamingExchange.USE_REALTIME_BOOK_TICKER;

@Slf4j
public abstract class WebSocketBasic {
	
	protected final BehaviorSubject<NodeOrder> subject = BehaviorSubject.create();
	protected final Map<CurrencyPair, StreamingExchange> exchangeMap = new HashMap<>();
	
	public abstract void streamValue();
	
	protected void init(ChangeType changeType, CurrencyProperties currencyProperties, ExchangeSpecification exchangeSpecification) {
		List<String> currencyPairs = currencyProperties.getExchanges().values().stream().filter(exchange -> changeType.toString().equals(exchange.getType()))
													   .flatMap(exchange -> exchange.getPairs().stream()).toList();
		for (String currencyPair : currencyPairs) {
			createExchangeForCurrencyPair(new CurrencyPair(currencyPair), exchangeSpecification);
		}
		
	}
	
	private void createExchangeForCurrencyPair(CurrencyPair currencyPair, ExchangeSpecification exchangeSpecification) {
		exchangeSpecification.setShouldLoadRemoteMetaData(true);
		exchangeSpecification.setExchangeSpecificParametersItem(USE_REALTIME_BOOK_TICKER, true);
		StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
		ProductSubscription subscription = ProductSubscription.create()
															  .addTrades(currencyPair)
															  .addOrderbook(currencyPair)
															  .build();
		exchange.connect(subscription).blockingAwait();
		exchange.getStreamingMarketDataService()
				.getTrades((Instrument) currencyPair)
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
	
}
