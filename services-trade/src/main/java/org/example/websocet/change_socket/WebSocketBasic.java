package org.example.websocet.change_socket;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.example.configuration.CurrencyProperties;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.instrument.Instrument;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.example.entity.collect.ChangeType;
import org.example.entity.collect.Pair;

@Slf4j
public abstract class WebSocketBasic {

    protected final BehaviorSubject<BigDecimal> subject = BehaviorSubject.create();
    protected final Map<CurrencyPair, Observable<BigDecimal>> exchangeMap = new HashMap<>();

    public abstract void streamValue();

    protected void init(ChangeType changeType, CurrencyProperties currencyProperties, ExchangeSpecification exchangeSpecification) {
        if (changeType == null) {
            throw new IllegalArgumentException("Change cannot be null");
        }
        if (currencyProperties == null) {
            throw new IllegalArgumentException("CurrencyProperties cannot be null");
        }
        if (exchangeSpecification == null) {
            throw new IllegalArgumentException("ExchangeSpecification cannot be null");
        }
        List<Pair> currencyPairs = currencyProperties.getExchanges().values().stream().filter(exchange ->
                        changeType.toString().equals(exchange.getType()))
                .flatMap(exchange -> exchange.getPairs().stream()).toList();
        for (Pair currencyPair : currencyPairs) {
            createExchangeForCurrencyPair(new CurrencyPair(currencyPair.getNamePair()), exchangeSpecification);
        }

    }

    private void createExchangeForCurrencyPair(CurrencyPair currencyPair, ExchangeSpecification exchangeSpecification) {
        if (currencyPair == null || exchangeSpecification == null) {
            throw new IllegalArgumentException("CurrencyPair and ExchangeSpecification cannot be null");
        }

        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
        ProductSubscription subscription = ProductSubscription.create()
                .addTrades(currencyPair)
                .addOrderbook(currencyPair)
                .build();

        exchange.connect(subscription).blockingAwait();
        Observable<BigDecimal> tradeObservable = exchange.getStreamingMarketDataService()
                .getTrades((Instrument) currencyPair)
                .map(Trade::getPrice)
                .doOnError(error -> {
                    log.error("Ошибка при получении данных: ", error);
                })
                .retryWhen(exponentialBackoff());
        tradeObservable.subscribe(
                subject::onNext,
                subject::onError
        );
        exchangeMap.put(currencyPair, tradeObservable);
    }

    private Function<Observable<Throwable>, Observable<?>> exponentialBackoff() {
        return errors -> errors.zipWith(Observable.range(1, 5), (error, attempt) -> attempt)
                .flatMap(attempt -> {
                    long delay = Math.min((long) Math.pow(2, attempt), 30);
                    log.warn("Переподключение через {} секунд из-за ошибки: ", delay);
                    return Observable.timer(delay, TimeUnit.SECONDS);
                });
    }
}
