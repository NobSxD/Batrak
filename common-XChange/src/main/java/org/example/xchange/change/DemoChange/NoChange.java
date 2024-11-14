package org.example.xchange.change.DemoChange;

import org.example.xchange.BasicChange;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;

import java.math.BigDecimal;

/**
 * **Класс NoChange предназначен для использования в виртуальной торговле.**
 * <p>
 * Этот класс позволяет пользователям выполнять торговые операции, используя выбранную ими биржу для получения валютных курсов,
 * при этом не создавая реальных экземпляров выбранной биржи. Таким образом, он обеспечивает выполнение торговых стратегий в
 * демо-режиме, эмулируя реальные рыночные условия.
 */
public class NoChange extends BasicChange {
    /**
     * **Пытается разместить маркет ордер.**
     * <p>
     * Поскольку этот класс предназначен для использования в демо-режиме, метод просто возвращает пустую строку,
     * не отправляя ордер на реальную биржу.
     *
     * @param marketOrder маркет ордер
     * @return пустая строка, так как ордер не отправляется на биржу
     */
    @Override
    public String placeMarketOrder(MarketOrder marketOrder) {
        return "";
    }

    @Override
    public BigDecimal coinConvertBay(BigDecimal amount, BigDecimal price, int scale) {
        return amount;
    }

    @Override
    public BigDecimal coinConvertSel(BigDecimal amount, BigDecimal price, int scale) {
        return amount;
    }

    /**
     * **Пытается разместить лимитный ордер.**
     * <p>
     * Подобно методу размещения маркет ордеров, этот метод также возвращает пустую строку.
     * Это предотвращает отправку ордеров на реальную биржу.
     *
     * @param limitOrder лимитный ордер
     * @return пустая строка, так как ордер не отправляется на биржу
     */
    @Override
    public String placeLimitOrder(LimitOrder limitOrder) {
        return "";
    }
}
