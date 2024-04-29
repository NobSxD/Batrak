package org.example.xchange.Binance.trede;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.util.Map;

public class BinanceTicet {
	public static void main(String[] args) throws IOException {

		// Use the factory to get Bitstamp exchange API using default settings
		Exchange exchange = BinanceDemoUtils.createExchange();
		raw(exchange);
	}

	private static void generic(MarketDataService marketDataService) throws IOException {

	//	Ticker ticker = marketDataService.getTicker(CurrencyPair.BTC_USD);

	//	System.out.println(ticker.toString());
	}

	private static void raw(Exchange exchange) throws IOException {

		AccountService accountService = exchange.getAccountService();
		AccountInfo accountInfo = accountService.getAccountInfo();
		Map<Currency, Balance> balances = accountInfo.getWallet().getBalances();

		for (Map.Entry<Currency, Balance> entry : balances.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue().getTotal());
		}
	}
}
