package org.example.websocet.change_socket;

import info.bitrich.xchangestream.bybit.BybitStreamingExchange;
import io.reactivex.rxjava3.core.Observable;
import org.example.configuration.CurrencyProperties;
import org.example.websocet.WebSocketChange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bybit.dto.BybitCategory;
import org.knowm.xchange.instrument.Instrument;

import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

import java.math.BigDecimal;

import org.example.entity.collect.ChangeType;

import org.springframework.stereotype.Component;

import static org.knowm.xchange.Exchange.USE_SANDBOX;

@Component
@RequiredArgsConstructor
public class WebSocketByBitChange extends WebSocketBasic implements WebSocketChange {
    private final CurrencyProperties currencyProperties;

    @Override
    @PostConstruct
    public void streamValue() {
        ExchangeSpecification exchangeSpecification = new
                BybitStreamingExchange().getDefaultExchangeSpecification();
        exchangeSpecification.setExchangeSpecificParametersItem(BybitStreamingExchange.EXCHANGE_TYPE,
                BybitCategory.LINEAR);
        exchangeSpecification.setExchangeSpecificParametersItem(USE_SANDBOX, true);
        exchangeSpecification.setShouldLoadRemoteMetaData(false);
        init(ChangeType.Bybit, currencyProperties, exchangeSpecification);
    }
    @Override
    public ChangeType getType() {
        return ChangeType.Bybit;
    }

    @Override
    public Observable<BigDecimal> exchangePair(Instrument instrument){
        return exchangeMap.get(instrument);
    }

}
