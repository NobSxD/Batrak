package org.example.xchange.finance;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class FinancialCalculator {

    /**
     * Метод для вычисления количества возможных сделок, которые можно выполнить на заданный депозит при заданной цене.
     *
     * @param deposit сумма депозита
     * @param price цена за единицу товара или валюты
     * @return количество возможных сделок
     */
    public static int sizeGridLevels(BigDecimal deposit, BigDecimal price){
        return deposit.divide(price, 0, RoundingMode.DOWN).intValue();
    }



    /**
     * Вычисляет новую цену, увеличенную на заданный процентный шаг.
     *
     * <p>Метод принимает первоначальную цену и процент увеличения в виде объектов `BigDecimal`.
     * Возвращает новую цену с тем же числом знаков после запятой, что и у валидации валюты USD.
     * </p>
     *
     * @param price исходная цена в виде `BigDecimal`.
     * @param step процент увеличения в виде `BigDecimal`.
     * @return результирующая цена с учётом увеличения, округлённая до нужного количества
     * знаков после запятой.
     */
    public static BigDecimal stepPrice(BigDecimal price, BigDecimal step){
        int scale = CurrencyConverter.validUsd(price).scale();
        return price.add(price.multiply(step).divide(new BigDecimal("100"))).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal increaseByPercentage(BigDecimal amount, double percentage) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount and percentage must not be null");
        }
        BigDecimal step = BigDecimal.valueOf(percentage);
        BigDecimal increase = amount.multiply(step);
        return amount.add(increase).setScale(2, RoundingMode.HALF_UP);
    }
    public static BigDecimal subtractPercentage(BigDecimal amount, double stepBay) {
        if (amount == null) {
            throw new IllegalArgumentException("Сумма и процент не могут быть null");
        }
        BigDecimal step = BigDecimal.valueOf(stepBay);
        return amount.subtract(amount.multiply(step));
    }

    public static BigDecimal addPercentage(BigDecimal lastPrice, double stepSell) {
        if (lastPrice == null) {
            throw new IllegalArgumentException("Последняя цена не может быть null");
        }
        BigDecimal step = BigDecimal.valueOf(stepSell);
        return lastPrice.add(lastPrice.multiply(step));
    }
}
