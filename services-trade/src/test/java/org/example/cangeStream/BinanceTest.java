package org.example.cangeStream;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.ExchangeSpecification;

import static info.bitrich.xchangestream.binance.BinanceStreamingExchange.USE_REALTIME_BOOK_TICKER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.knowm.xchange.Exchange.USE_SANDBOX;

@Tag("integration")
public class BinanceTest {
    @Test
    void checkStream(){
        ExchangeSpecification exchangeSpecification = new ExchangeSpecification(BinanceStreamingExchange.class);
        exchangeSpecification.setExchangeSpecificParametersItem(USE_SANDBOX, true);
        exchangeSpecification.setShouldLoadRemoteMetaData(true);
        exchangeSpecification.setExchangeSpecificParametersItem(USE_REALTIME_BOOK_TICKER, true);
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
        assertThat(exchange).isNotNull();
    }
}
