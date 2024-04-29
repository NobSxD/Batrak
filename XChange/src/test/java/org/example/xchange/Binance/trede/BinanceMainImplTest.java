package org.example.xchange.Binance.trede;

import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.account.AccountBinance;
import org.example.xchange.Binance.BinanceMainImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;

import java.math.BigDecimal;




class BinanceMainImplTest {

	NodeUser nodeUser;
	Account account;
	@BeforeEach
	void setAp(){
	//	nodeUser = new NodeUser();
		account = new AccountBinance();
		account.setNameChange("Bot");
		account.setPublicApiKey("sgZ7Dh5wGnDKqELXibieKDFuYI2gApRw9lJAdzwdNvdJ6KDOoIQcZLdnw0vMeGE2");
		account.setSecretApiKey("DLe97nf0mcG5XKDhcDZk4Qk9bOLxnGFPXOObXx2Vo1TiOXLqypSjm6msMjJGXmj7");
		nodeUser.setAccount(account);

	}

	@Test
	void marketOrder() {
		BinanceMainImpl binanceMain = new BinanceMainImpl(nodeUser);
		CurrencyPair currencyPair = new CurrencyPair(Currency.BNB, Currency.USDT);
		String s = binanceMain.marketOrder(Order.OrderType.BID, BigDecimal.valueOf(11), currencyPair);
		System.out.println(s);
	}

}