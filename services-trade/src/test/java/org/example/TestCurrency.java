package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.Currency;

public class TestCurrency {
    @Test
    void currencyNewBinance(){
        String BTC_USDT =  "BTC-USDT";
        String BNB_USDT =  "BNB-USDT";
        String NOT_USDT =  "NOT-USDT";
        String SOL_USDT =  "SOL-USDT";
        String ETH_USDT =  "ETH-USDT";
        Currency currencyBTC = new Currency(BTC_USDT);
        Currency currencyBNB = new Currency(BNB_USDT);
        Currency currencyNOT = new Currency(NOT_USDT);
        Currency currencySOL = new Currency(SOL_USDT);
        Currency currencyETH = new Currency(ETH_USDT);

        Assertions.assertNotNull(currencyBTC);
        Assertions.assertNotNull(currencyBNB);
        Assertions.assertNotNull(currencyNOT);
        Assertions.assertNotNull(currencySOL);
        Assertions.assertNotNull(currencyETH);

    }
}
