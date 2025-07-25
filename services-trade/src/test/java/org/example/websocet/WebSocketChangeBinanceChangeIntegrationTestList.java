package org.example.websocet;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.exampel.crypto.CryptoUtils;
import org.example.configuration.CurrencyProperties;
import org.example.entity.Account;
import org.example.entity.ConfigTrade;
import org.example.entity.NodeUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.mockito.Mock;

import static info.bitrich.xchangestream.binance.BinanceStreamingExchange.USE_REALTIME_BOOK_TICKER;

@Tag("integration")
class WebSocketChangeBinanceChangeIntegrationTestList {
	NodeUser nodeUser;
	Account account;
	@Mock
	private CurrencyProperties currencyProperties;


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