package org.example.cangeStream;

import info.bitrich.xchangestream.bybit.BybitStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bybit.dto.BybitCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.knowm.xchange.Exchange.USE_SANDBOX;

@Tag("integration")
public class BiabitTest {
    @Test
    void checkStream(){
        ExchangeSpecification exchangeSpecification = new
                BybitStreamingExchange().getDefaultExchangeSpecification();
        exchangeSpecification.setExchangeSpecificParametersItem(
                BybitStreamingExchange.EXCHANGE_TYPE, BybitCategory.LINEAR);
        exchangeSpecification.setExchangeSpecificParametersItem(USE_SANDBOX, true);

        exchangeSpecification.setShouldLoadRemoteMetaData(false);
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
        assertThat(exchange).isNotNull();
    }
}
