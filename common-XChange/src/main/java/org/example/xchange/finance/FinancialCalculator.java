package org.example.xchange.finance;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FinancialCalculator {

    public static BigDecimal maxAmount(List<LimitOrder> limitOrders) {
        int index = 0;
        BigDecimal max;
        BigDecimal maxOriginalAmount = BigDecimal.ZERO;
        LimitOrder orderWithMaxOriginalAmount = null;

        for (int i = 0; i < limitOrders.size(); i++) {
            LimitOrder currentOrder = limitOrders.get(i);
            if (currentOrder.getOriginalAmount().compareTo(maxOriginalAmount) > 0) {
                maxOriginalAmount = currentOrder.getOriginalAmount();
                orderWithMaxOriginalAmount = currentOrder;
                index = i;
            }
        }

        if (orderWithMaxOriginalAmount != null) {
            if (index > 0) {
                max = limitOrders.get(index - 1).getLimitPrice();
            } else {
                max = limitOrders.get(index).getLimitPrice();
            }
        } else {
            max = limitOrders.get(0).getLimitPrice();
        }
        return max;
    }


    public static BigDecimal maxAmount(List<LimitOrder> limitOrders, BigDecimal minPrice) {
        int index = 0;
        BigDecimal maxOriginalAmount = BigDecimal.ZERO;
        LimitOrder orderWithMaxOriginalAmount = null;
        for (int i = 0; i < limitOrders.size(); i++) {
            if (limitOrders.get(i).getLimitPrice().compareTo(minPrice) > 0 && limitOrders.get(i).getOriginalAmount().compareTo(maxOriginalAmount) > 0) {
                maxOriginalAmount = limitOrders.get(i).getOriginalAmount();
                orderWithMaxOriginalAmount = limitOrders.get(i);
                index = i;
            }
        }

        if (orderWithMaxOriginalAmount != null) {
            if (index > 0) {
                return limitOrders.get(index - 1).getLimitPrice();
            }
            return limitOrders.get(index).getLimitPrice();
        }
        return minPrice;
    }

    public static List<BigDecimal> gridLevels(BigDecimal initialRate, BigDecimal percentageStep, int size, int scale) {
        List<BigDecimal> gridLevels = new ArrayList<>();
        // Добавляем начальную цену в список
        BigDecimal currentRate = initialRate;
        gridLevels.add(currentRate);

        BigDecimal step = percentageStep.divide(new BigDecimal("100"), scale, RoundingMode.HALF_UP);
        for (int i = 1; i < size; i++) {
            // Вычисляем следующую цену
            currentRate = currentRate.subtract(currentRate.multiply(step).setScale(scale, RoundingMode.HALF_UP));
            gridLevels.add(currentRate);
        }
        log.info("Сетка ордеров сформированна начальная цена:{}. Размер:{}. Шаг:{}%.", initialRate, size, percentageStep);
        return gridLevels;
    }

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

    public static BigDecimal increaseByPercentage(BigDecimal amount, BigDecimal percentage) {
        if (amount == null || percentage == null) {
            throw new IllegalArgumentException("Amount and percentage must not be null");
        }
        // Увеличиваем сумму на процент
        BigDecimal increase = amount.multiply(percentage);
        return amount.add(increase).setScale(2, RoundingMode.HALF_UP);
    }
}
