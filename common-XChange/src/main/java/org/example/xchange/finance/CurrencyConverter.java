package org.example.xchange.finance;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CurrencyConverter {

    public static BigDecimal convertCurrency(BigDecimal price, BigDecimal usdt, int scale) {
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        BigDecimal result = usdt.divide(price, scale, roundingMode);
        log.info("usdt={} / price={} равно={}", usdt, price, result);
        return result;

    }

    public static BigDecimal convertUsdt(BigDecimal price, BigDecimal coinAmount) {
        int scale = 2;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        BigDecimal result = coinAmount.multiply(price).setScale(scale, roundingMode);
        log.info("Криптавалюта={} * price={} равно={}", coinAmount, price, result);
        return result;

    }

    public static BigDecimal validUsd(BigDecimal value) {
        if (value == null) {
            return null;
        }

        // Убираем все незначащие нули в дробной части
        value = value.stripTrailingZeros();

        // Для целых чисел >= 1 оставляем 2 знака после запятой
        if (value.compareTo(BigDecimal.ONE) >= 0) {
            return value.setScale(2, RoundingMode.DOWN);
        } else {
            // Создаём регулярное выражение для поиска первой значащей цифры, которая не равна нулю
            Pattern pattern = Pattern.compile("0?(\\.0*)([1-9])");
            Matcher matcher = pattern.matcher(value.toPlainString());

            if (matcher.find()) {
                // Находим позицию первой значащей цифры
                int firstSignificantDigitIndex = matcher.start(2);
                // Общее количество знаков после точки
                int totalScale = firstSignificantDigitIndex + 5 - matcher.start(1);
                return value.setScale(totalScale, RoundingMode.DOWN).stripTrailingZeros();
            }

            return value; // Если значащих цифр не найдено, возвращаем исходное значение
        }
    }

}
