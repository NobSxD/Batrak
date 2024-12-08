package org.example.xchange.finance;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class FinancialCalculator {

    public static BigDecimal increaseByPercentage(BigDecimal currentPrice, double percentage) {
        if (currentPrice == null) {
            throw new IllegalArgumentException("Amount and percentage must not be null");
        }
        BigDecimal step = BigDecimal.valueOf(percentage);
        BigDecimal increase = currentPrice.multiply(step);
        return currentPrice.add(increase);
    }
    public static BigDecimal subtractPercentage(BigDecimal currentPrice, double stepBay) {
        if (currentPrice == null) {
            throw new IllegalArgumentException("Сумма и процент не могут быть null");
        }
        BigDecimal step = BigDecimal.valueOf(stepBay);
        return currentPrice.subtract(currentPrice.multiply(step));
    }

    public static BigDecimal addPercentage(BigDecimal lastPrice, double stepSell) {
        if (lastPrice == null) {
            throw new IllegalArgumentException("Последняя цена не может быть null");
        }
        BigDecimal step = BigDecimal.valueOf(stepSell);
        return lastPrice.add(lastPrice.multiply(step));
    }
}
