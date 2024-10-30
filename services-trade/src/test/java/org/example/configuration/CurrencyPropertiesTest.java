package org.example.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest // Используется для настройки тестового контекста
@Import(CurrencyProperties.class) // Импортируем класс конфигурации для теста
@EnableConfigurationProperties
class CurrencyPropertiesTest {


	@Autowired
	private CurrencyProperties currencyProperties;


	@Test
	void testCurrencyExchangesBinance() {
		assertThat(currencyProperties.getExchanges()).isNotNull();
		assertThat(currencyProperties.getExchanges()).containsKey("binance");

		CurrencyProperties.ExchangeProperties binance = currencyProperties.getExchanges().get("binance");

		assertThat(binance).isNotNull();
		assertThat(binance.getType()).isEqualTo("binance");
		assertThat(binance.getPairs().get(0).getScale()).isEqualTo(5);
		assertThat(binance.getPairs().get(1).getScale()).isEqualTo(3);

		assertThat(binance.getPairs()).hasSize(2);

		assertThat(binance.getPairs().get(0).getNamePair()).isEqualTo("BTC-USDT").info.description("BTC-USDT");
		assertThat(binance.getPairs().get(1).getNamePair()).isEqualTo("BNB-USDT").info.description("BNB-USDT");
	}

	@Test
	void testCurrencyExchangesByBit() {
		assertThat(currencyProperties.getExchanges()).isNotNull();
		assertThat(currencyProperties.getExchanges()).containsKey("bybit");

		CurrencyProperties.ExchangeProperties bybit = currencyProperties.getExchanges().get("bybit");

		assertThat(bybit).isNotNull();
		assertThat(bybit.getType()).isEqualTo("bybit");
		assertThat(bybit.getPairs().get(0).getScale()).isEqualTo(5);

		assertThat(bybit.getPairs()).hasSize(1);
		assertThat(bybit.getPairs().get(0).getNamePair()).isEqualTo("BTC-USDT").info.description("BTC-USDT");
	}
}