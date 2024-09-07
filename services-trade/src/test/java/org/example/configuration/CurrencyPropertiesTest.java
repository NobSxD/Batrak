package org.example.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
class CurrencyPropertiesTest {
	
	private final CurrencyProperties currencyProperties;
	
	CurrencyPropertiesTest(CurrencyProperties currencyProperties) {
		this.currencyProperties = currencyProperties;
	}
	
	@Test
	void testCurrencyExchangesLoaded() {
		assertThat(currencyProperties.getExchanges()).isNotNull();
		assertThat(currencyProperties.getExchanges()).containsKey("binance");
		
		CurrencyProperties.ExchangeProperties binance = currencyProperties.getExchanges().get("binance");
		
		assertThat(binance).isNotNull();
		assertThat(binance.getType()).isEqualTo("binance");
		assertThat(binance.getScale()).isEqualTo(8);
		
		assertThat(binance.getPairs()).hasSize(2);
		assertThat(binance.getPairs()).extracting("currencyPair")
									  .containsExactlyInAnyOrder("BTC-USDT", "ETH-USDT");
	}
	
	@Test
	void testExchangeHasCorrectScale() {
		CurrencyProperties.ExchangeProperties binance = currencyProperties.getExchanges().get("binance");
		int expectedScale = 8;
		assertThat(binance.getScale()).isEqualTo(expectedScale);
	}
}