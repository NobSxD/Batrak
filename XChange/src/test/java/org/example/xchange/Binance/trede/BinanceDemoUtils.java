package org.example.xchange.Binance.trede;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;

public class BinanceDemoUtils {
	public static Exchange createExchange() {

		ExchangeSpecification exSpec = new BinanceExchange().getDefaultExchangeSpecification();
		exSpec.setUserName("Bot");
		exSpec.setApiKey("sgZ7Dh5wGnDKqELXibieKDFuYI2gApRw9lJAdzwdNvdJ6KDOoIQcZLdnw0vMeGE2");
		exSpec.setSecretKey("DLe97nf0mcG5XKDhcDZk4Qk9bOLxnGFPXOObXx2Vo1TiOXLqypSjm6msMjJGXmj7");
		return ExchangeFactory.INSTANCE.createExchange(exSpec);
	}
}
