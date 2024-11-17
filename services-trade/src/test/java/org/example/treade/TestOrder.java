package org.example.treade;

import org.exampel.crypto.CryptoUtils;
import org.example.xchange.DTO.ChangeUser;
import org.example.xchange.change.Binance.BinanceMainImpl;
import org.example.xchange.finance.CurrencyConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.util.List;

@Tag("integration")
public class TestOrder {
    ChangeUser changeUser;
    @BeforeEach
    void setAp(){
        CryptoUtils cryptoUtils = new CryptoUtils();
        changeUser = ChangeUser.builder()
                .botName("Bot")
                .apiKey(cryptoUtils.encryptMessage(System.getenv("pKey")))
                .secretKey(cryptoUtils.encryptMessage(System.getenv("sKey")))
                .build();

    }
    @Test
    void placeLimitOrderAndConvertFalse(){
        BinanceMainImpl binanceMain = new BinanceMainImpl(changeUser);
        CurrencyPair instrument = new CurrencyPair("BTC-USDT");
        BigDecimal price = CurrencyConverter.convertCurrency(new BigDecimal("53231.39000000"), new BigDecimal("11.2"), 5);
        List<BigDecimal> priceAndAmount = List.of(new BigDecimal("50000.39000000"),price);
        LimitOrder order = binanceMain.createOrder(instrument, priceAndAmount, Order.OrderType.BID, 5);
        String s = binanceMain.placeLimitOrder(order);
        System.out.println(s);
    }
    @Test
    void cancelOrder() throws InterruptedException {
        BinanceMainImpl binanceMain = new BinanceMainImpl(changeUser);
        CurrencyPair instrument = new CurrencyPair("BTC-USDT");
        BigDecimal price = CurrencyConverter.convertCurrency(new BigDecimal("53231.39000000"), new BigDecimal("11.2"), 5);
        List<BigDecimal> priceAndAmount = List.of(new BigDecimal("50000.39000000"),price);
        LimitOrder order = binanceMain.createOrder(instrument, priceAndAmount, Order.OrderType.BID, 5);
        String s = binanceMain.placeLimitOrder(order);
        Thread.sleep(2000);
        binanceMain.cancelOrder("BTC/USDT",s);
        System.out.println(s);
    }

}
