package org.example.websocet.change_socket;

import org.example.configuration.CurrencyProperties;
import org.example.entity.NodeOrder;
import org.example.entity.enams.menu.MenuChange;
import org.example.websocet.WebSocketChange;

import info.bitrich.xchangestream.bybit.BybitStreamingExchange;
import io.reactivex.rxjava3.core.Observable;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bybit.dto.BybitCategory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
        init(MenuChange.Bybit, currencyProperties, exchangeSpecification);
    }

    @Override
    public Observable<NodeOrder> getCurrencyRateStream() {
        return subject;
    }

    @Override
    public MenuChange getType() {
        return MenuChange.Bybit;
    }

}
