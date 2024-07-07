package org.example.websocet;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.apache.log4j.Logger;
import org.example.observer.ObserverPrice;
import org.example.observer.Subject;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.instrument.Instrument;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static info.bitrich.xchangestream.binance.BinanceStreamingExchange.USE_REALTIME_BOOK_TICKER;


@Component
public class WebSocketBinanceChange implements Subject{
	private final Subject dataBtcUsdt;

	private final Logger logger = Logger.getLogger(WebSocketBinanceChange.class);

	public WebSocketBinanceChange(Subject dataBtcUsdt) throws InterruptedException {
		this.dataBtcUsdt = dataBtcUsdt;
		streamBtcUSDT();
	}

	public void streamBtcUSDT() throws InterruptedException {
		final ExchangeSpecification exchangeSpecification = new ExchangeSpecification(BinanceStreamingExchange.class);
		exchangeSpecification.setShouldLoadRemoteMetaData(true);
		exchangeSpecification.setExchangeSpecificParametersItem(USE_REALTIME_BOOK_TICKER, true);
		StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
		ProductSubscription subscription = ProductSubscription.create().addTrades(CurrencyPair.BTC_USDT).addOrderbook(CurrencyPair.BTC_USDT).build();
		exchange.connect(subscription).blockingAwait();

		exchange.getStreamingMarketDataService()
				.getTrades(CurrencyPair.BTC_USDT)
				.subscribe(trade -> {
					dataBtcUsdt.setPrice(trade.getPrice(), trade.getType(), trade.getInstrument());
					logger.info(trade.toString());
				});
		Thread.sleep(2000);
	}

	@Override
	public void registerObserver(ObserverPrice observerPrice) {

	}

	@Override
	public void removeObserver(ObserverPrice observerPrice) {

	}

	@Override
	public void notifyObservers() {

	}

	@Override
	public void setPrice(BigDecimal price, Order.OrderType orderType, Instrument instrument) {

	}
}
