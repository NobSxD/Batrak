package org.example.xchange.Binance.trede;

import org.exampel.crypto.CryptoUtils;
import org.example.xchange.DTO.ChangeUser;
import org.example.xchange.change.Binance.BinanceMainImpl;
import org.example.xchange.finance.CurrencyConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceTicker24h;
import org.knowm.xchange.binance.service.BinanceMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


@Tag("integration")
class BinanceMainImpIntegratio {
    ChangeUser changeUser;
    @BeforeEach
    void setAp() {
        CryptoUtils cryptoUtils = new CryptoUtils();
        changeUser = ChangeUser.builder()
                .botName("Bot")
                .apiKey(cryptoUtils.encryptMessage(System.getenv("pKey")))
                .secretKey(cryptoUtils.encryptMessage(System.getenv("sKey")))
                .build();

    }

    @Test
    public void generatorDTO() throws IOException {
        //BinanceMainImpl binanceMain = new BinanceMainImpl(nodeUser);
        Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class);

        // Interested in the public market data feed (no authentication)
        MarketDataService marketDataService = bitstamp.getMarketDataService();


        Ticker ticker = marketDataService.getTicker((Instrument) CurrencyPair.BTC_USDT);

        System.out.println(ticker.toString());

        BinanceMarketDataServiceRaw raw = (BinanceMarketDataServiceRaw) marketDataService;
        BinanceTicker24h bitstampTicker = raw.ticker24hAllProducts((Instrument) CurrencyPair.BTC_USDT);

        System.out.println(bitstampTicker.toString());

    }

    @Test
    void createOrder() {
        BinanceMainImpl binanceMain = new BinanceMainImpl(changeUser);
        Instrument instrument = new CurrencyPair("BTC-USDT");
        List<BigDecimal> priceAndAmount = List.of(new BigDecimal("50000"), new BigDecimal("10.5"));
        LimitOrder order = binanceMain.createOrder(instrument, priceAndAmount, Order.OrderType.BID);

    }
    @Test
    void marketOrder(){
        BinanceMainImpl binanceMain = new BinanceMainImpl(changeUser);
        Instrument instrument = new CurrencyPair("BTC-USDT");
        MarketOrder marketOrder = binanceMain.createMarketOrder(Order.OrderType.BID, new BigDecimal("0.00017"), instrument);
        String order = binanceMain.placeMarketOrder(marketOrder);
        System.out.println(order);
    }

    @Test
    void placeLimitOrder() {
        BinanceMainImpl binanceMain = new BinanceMainImpl(changeUser);
        Instrument instrument = new CurrencyPair("BTC-USDT");
        List<BigDecimal> priceAndAmount = List.of(new BigDecimal("50000"), new BigDecimal("0.00020"));
        LimitOrder order = binanceMain.createOrder(instrument, priceAndAmount, Order.OrderType.BID);
        binanceMain.placeLimitOrder(order);
    }

    @Test
    void placeLimitOrderAndConvert() {
        BinanceMainImpl binanceMain = new BinanceMainImpl(changeUser);
        Instrument instrument = new CurrencyPair("BTC-USDT");
        BigDecimal price = CurrencyConverter.convertCurrency(new BigDecimal("53231"), new BigDecimal("11.2"), 5);
        List<BigDecimal> priceAndAmount = List.of(new BigDecimal("50000"), price);
        LimitOrder order = binanceMain.createOrder(instrument, priceAndAmount, Order.OrderType.BID);
        binanceMain.placeLimitOrder(order);
    }

    @Test
    void placeLimitOrderAndConvertFalse() {
        BinanceMainImpl binanceMain = new BinanceMainImpl(changeUser);
        CurrencyPair instrument = new CurrencyPair("BTC-USDT");
        BigDecimal price = CurrencyConverter.convertCurrency(new BigDecimal("53231.39000000"), new BigDecimal("11.2"), 5);
        List<BigDecimal> priceAndAmount = List.of(new BigDecimal("50000.39000000"), price);
        LimitOrder order = binanceMain.createOrder(instrument, priceAndAmount, Order.OrderType.BID);
        String s = binanceMain.placeLimitOrder(order);
        System.out.println(s);
    }

    @Test
    void pair() {
        CurrencyPair currencyPair = new CurrencyPair("ER-TTT");
        System.out.println(currencyPair);

    }

    @Test
    void bigDecimal() {
        String a = "123";
        Double d = Double.valueOf(a);
        BigDecimal bigDecimal = BigDecimal.valueOf(d);
        System.out.println(bigDecimal);
    }

    @Test
    void orderBokAsks() {
        BinanceMainImpl binanceMain = new BinanceMainImpl(changeUser);
        OrderBook orderBook = binanceMain.orderBooksLimitOrders(25, "BTC-USDT");
        orderBook.getAsks();
    }

}