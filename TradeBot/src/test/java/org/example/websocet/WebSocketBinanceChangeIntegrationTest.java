package org.example.websocet;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.rxjava3.observers.TestObserver;
import org.exampel.crypto.CryptoUtils;
import org.example.entity.Account;
import org.example.entity.ConfigTrade;
import org.example.entity.NodeUser;
import org.example.observer.ObserverPrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;

import java.math.BigDecimal;

import static info.bitrich.xchangestream.binance.BinanceStreamingExchange.USE_REALTIME_BOOK_TICKER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


class WebSocketBinanceChangeIntegrationTest {
	NodeUser nodeUser;
	Account account;


	@BeforeEach
	void setAp(){

		nodeUser = new NodeUser();
		account = new Account();
		ConfigTrade configTrade = new ConfigTrade();
		CryptoUtils cryptoUtils = new CryptoUtils();
		account.setNameAccount("Bot");
		account.setPublicApiKey(cryptoUtils.encryptMessage(System.getenv("pKey")));
		account.setSecretApiKey(cryptoUtils.encryptMessage(System.getenv("sKey")));
		nodeUser.setConfigTrade(configTrade);
		nodeUser.setAccount(account);

	}

	@Test
	public void testSetPriceAndNotifyObservers() throws InterruptedException {
		// Создание мок объектов ObserverPrice
		ObserverPrice observer1 = mock(ObserverPrice.class);
		ObserverPrice observer2 = mock(ObserverPrice.class);

		// Создание WebSocketBinanceChange
		WebSocketBinanceChange webSocket = new WebSocketBinanceChange();

		// Регистрация наблюдателей

		// Проверка, что метод update был вызван для всех зарегистрированных наблюдателей
		verify(observer1, times(1)).update(BigDecimal.valueOf(10000), Order.OrderType.BID, CurrencyPair.BTC_USDT);
		verify(observer2, times(1)).update(BigDecimal.valueOf(10000), Order.OrderType.BID, CurrencyPair.BTC_USDT);
	}

	@Test
	public void testAddObserver() throws InterruptedException {
		WebSocketBinanceChange webSocket = new WebSocketBinanceChange();
		TestObserver<BigDecimal> testObserver = new TestObserver<>();
		webSocket.addObserver(testObserver);

		// Проверяем, что наблюдатель успешно добавлен
		assertTrue( webSocket.hasObservers());
	}

	@Test
	public void testRemoveObserver() throws InterruptedException {
		WebSocketBinanceChange webSocket = new WebSocketBinanceChange();
		TestObserver<BigDecimal> testObserver = new TestObserver<>();
		webSocket.addObserver(testObserver);

		webSocket.removeObserver(testObserver);

		// Проверяем, что наблюдатель успешно удален
		assertFalse( webSocket.hasObservers());
	}
	@Test
	public void testRemoveObservers() throws InterruptedException {
		WebSocketBinanceChange webSocket = new WebSocketBinanceChange();
		TestObserver<BigDecimal> testObserver = new TestObserver<>();
		TestObserver<BigDecimal> testObserver1 = new TestObserver<>();
		webSocket.addObserver(testObserver);
		webSocket.addObserver(testObserver1);
		webSocket.removeObserver(testObserver);

		// Проверяем, что наблюдатель успешно удален, но только конкретный
		assertTrue( webSocket.hasObservers());
	}


	@Test
	void testWebSocketStreamData() throws InterruptedException {
		final ExchangeSpecification exchangeSpecification =
				new ExchangeSpecification(BinanceStreamingExchange.class);
		exchangeSpecification.setShouldLoadRemoteMetaData(true);
		exchangeSpecification.setExchangeSpecificParametersItem(USE_REALTIME_BOOK_TICKER, true);
		StreamingExchange exchange =
				StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
		ProductSubscription subscription =
				exchange.getExchangeInstruments().stream()
						.filter(instrument -> instrument instanceof CurrencyPair)
						.limit(50)
						.reduce(
								ProductSubscription.create(),
								ProductSubscription.ProductSubscriptionBuilder::addTicker,
								(productSubscriptionBuilder, productSubscriptionBuilder2) -> {
									throw new UnsupportedOperationException();
								})
						.build();
		exchange.connect(subscription).blockingAwait();
		exchange
				.getStreamingMarketDataService()
				.getTicker(subscription.getTicker().get(0))
				.subscribe(System.out::println);
		Thread.sleep(2000);
	}

}