package org.example.xchange.change.Binance.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "currency-binance")
@Getter
@Setter
public class CurrencyProperties {
	private List<String> pairs;

}
