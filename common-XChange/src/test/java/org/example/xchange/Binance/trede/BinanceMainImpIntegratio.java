package org.example.xchange.Binance.trede;


import org.exampel.crypto.CryptoUtils;
import org.example.entity.Account;
import org.example.entity.ConfigTrade;
import org.example.entity.NodeUser;
import org.example.xchange.DTO.ChangeUser;
import org.example.xchange.change.Binance.BinanceMainImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceTicker24h;
import org.knowm.xchange.binance.service.BinanceMarketDataServiceRaw;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.math.BigDecimal;


class BinanceMainImpIntegratio {

	NodeUser nodeUser;
	Account account;
	ChangeUser changeUser;

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
		changeUser = ChangeUser.builder()
				.userName(nodeUser.getUsername())
				.apiKey(nodeUser.getAccount().getPublicApiKey())
				.secretKey(nodeUser.getAccount().getSecretApiKey())
				.botName(nodeUser.getAccount().getNameAccount())
				.build();

	}
	@Test
	public void generatorDTO() throws IOException {
		//BinanceMainImpl binanceMain = new BinanceMainImpl(nodeUser);
		Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class);

		// Interested in the public market data feed (no authentication)
		MarketDataService marketDataService = bitstamp.getMarketDataService();



		Ticker ticker = marketDataService.getTicker((Instrument) CurrencyPair.BTC_USDT);

		System.out.println(ticker.toString());

		BinanceMarketDataServiceRaw raw = (BinanceMarketDataServiceRaw) marketDataService;
		BinanceTicker24h bitstampTicker = raw.ticker24hAllProducts((Instrument) CurrencyPair.BTC_USDT);

		System.out.println(bitstampTicker.toString());

	}

	@Test
	void marketOrder() {
		BinanceMainImpl binanceMain = new BinanceMainImpl(changeUser);
		CurrencyPair currencyPair = new CurrencyPair(Currency.BTC, Currency.USDT);
		String s = binanceMain.marketOrder(Order.OrderType.ASK, BigDecimal.valueOf(11), currencyPair);
		System.out.println(s);
	}

	@Test
	void limitOrder() {
		BinanceMainImpl binanceMain = new BinanceMainImpl(changeUser);

		Instrument instrument = new CurrencyPair("BTC-USDT");
		String s = binanceMain.limitOrder(Order.OrderType.BID, BigDecimal.valueOf(11), new BigDecimal("50000"), instrument);
		System.out.println(s);
	}

	@Test
	void pair(){
		CurrencyPair currencyPair = new CurrencyPair("ER-TTT");
		System.out.println(currencyPair);

	}
	@Test
	void bigDecimal(){
		String a = "123";
		Double d = Double.valueOf(a);
		BigDecimal bigDecimal = BigDecimal.valueOf(d);
		System.out.println(bigDecimal);
	}

	@Test
	void orderBokAsks(){
		ChangeUser changeUser = ChangeUser.builder()
				.userName(nodeUser.getUsername())
				.apiKey(nodeUser.getAccount().getPublicApiKey())
				.secretKey(nodeUser.getAccount().getSecretApiKey())
				.botName(nodeUser.getAccount().getNameAccount())
				.build();
		BinanceMainImpl binanceMain = new BinanceMainImpl(changeUser);
		OrderBook orderBook = binanceMain.orderBooksLimitOrders(25, changeUser.getPairName());
		orderBook.getAsks();
	}

}