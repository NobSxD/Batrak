package org.example.configuration;

import org.example.entity.collect.Pair;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "currency")
public class CurrencyProperties {
    private Map<String, ExchangeProperties> exchanges;

    @Setter
    @Getter
    public static class ExchangeProperties {
        private String type;
        private List<Pair> pairs;
        private int scale;
    }
}
