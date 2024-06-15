package org.example.xchange.Binance.trede;

import org.example.crypto.CryptoUtils;
import org.example.entity.NodeUser;
import org.example.entity.SettingsTrade;
import org.example.entity.account.Account;
import org.example.xchange.Binance.BinanceMainImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;

import java.math.BigDecimal;




class BinanceMainImplTest {

	NodeUser nodeUser;
	Account account;

	@BeforeEach
	void setAp(){
		nodeUser = new NodeUser();
		account = new AccountBinance();
		SettingsTrade settingsTrade  = new SettingsTrade();
		CryptoUtils cryptoUtils = new CryptoUtils();
		account.setNameChange("Bot");
		account.setPublicApiKey(cryptoUtils.encryptMessage(System.getenv("pKey")));
		account.setSecretApiKey(cryptoUtils.encryptMessage(System.getenv("sKey")));
		nodeUser.setSettingsTrade(settingsTrade);
		nodeUser.setAccount(account);

	}

	@Test
	void marketOrder() {
		BinanceMainImpl binanceMain = new BinanceMainImpl(nodeUser);
		CurrencyPair currencyPair = new CurrencyPair(Currency.BNB, Currency.USDT);
		String s = binanceMain.marketOrder(Order.OrderType.BID, BigDecimal.valueOf(11), currencyPair);
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
		String b = "text";
		Double d = Double.valueOf(a);
		//Double c = Double.valueOf(b);
		BigDecimal bigDecimal = BigDecimal.valueOf(d);
		//BigDecimal bigDecimal2 = BigDecimal.valueOf(c);

		System.out.println(bigDecimal);
		//System.out.println(bigDecimal2);
	}

	@Test
	void orderBokAsks(){
		BinanceMainImpl binanceMain = new BinanceMainImpl(nodeUser);
		OrderBook orderBook = binanceMain.orderBooksLimitOrders(25, nodeUser);
		orderBook.getAsks();
	}

}