package org.example.xchange.finance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrencyConverterTest {

	@ParameterizedTest
	@CsvSource({

			"67773.2, 67.74, 5, 0.001",
			"50000, 50, 6, 0.001",
			"45000, 90, 8, 0.002",
			"10000, 100, 5, 0.01",
			"60000, 60, 4, 0.001",
			"70000, 70, 5, 0.001",
			"45000, 45.5, 6, 0.001011",
			"61000, 61, 10, 0.001",
			"67773.2, 33.87, 5, 0.0005",
			"5000, 5, 3, 0.001",
			"0.15173, 100.00, 2, 659.07",
			"0.15264, 100.60, 2 , 659.07",
			"0.15175, 100.60, 2,  662.93",
			"0.15019, 100.00, 2, 665.82"
	})

	void convertUsdt(String priceBTC, String cash, int scale, String expectedBTC) {
		// Перевод значений из строкового представления в BigDecimal
		BigDecimal price = new BigDecimal(priceBTC);
		BigDecimal cashAmount = new BigDecimal(cash);
		BigDecimal expected = new BigDecimal(expectedBTC);

		// Использование метода конвертации
		BigDecimal result = CurrencyConverter.convertCurrency(price, cashAmount, scale);

		// Сравнение результата с ожидаемым значением
		assertEquals(expected, result);
	}
	@ParameterizedTest
	@CsvSource({
			// Входное значение, ожидаемое значение
			"15.89189456, 15.89",          // Стандартная проверка
			"0.1547, 0.1547",              // Значение с 4 знаками после запятой
			"00.8912364, 0.891236",        // Ноль в начале, 6 знаков
			"00.00887456, 0.00887456",     // Малое число, 8 знаков
			"0.00000145621, 0.00000145621",// Очень малое число, 11 знаков
			"0.000000014564, 0.000000014564", // Очень малое число, 12 знаков
			"0.00000000016212, 0.00000000016212", // Невероятно малое число
			"0.000000000001234, 0.000000000001234", // Минимально заметное число
			"0.000000000000014562100, 0.0000000000000145621", // С отбрасыванием лишних нулей
			"1.8978757458945, 1.89",      // Большое число с отбрасыванием
			"150.8987574, 150.89",        // Крупное число
			"15.89074575, 15.89",         // Классический случай округления
			"10.12345, 10.12",            // Тест округления до двух знаков
			"20.19999, 20.19",            // Проверка на округление вниз
			"5.000000, 5.00",             // Ровное значение без дробной части
			"0.00001, 0.00001",           // Относительно малое число
			"12345.678901, 12345.67",     // Очень крупное число с дробной частью
			"0.1, 0.1",                   // Простое значение
			"9999999.9999999, 9999999.99",// Очень крупное значение с обрезанием дроби
			"0.0000000000000000001, 0.0000000000000000001"  // Практически нулевое значение
	})
	void checkValidUsd(String input, String expected) {
		BigDecimal inputValue = new BigDecimal(input);
		BigDecimal expectedValue = new BigDecimal(expected);

		BigDecimal result = CurrencyConverter.validScale(inputValue);

		assertEquals(expectedValue, result);
	}
	@Test
	void scale(){
		RoundingMode roundingMode = RoundingMode.HALF_UP;
		BigDecimal test;
		BigDecimal test2 = new BigDecimal("14.6789345").setScale(3,roundingMode);
		test = test2;
		assertEquals(test.scale(),3);
		
	}

	@Test
	public void convertCurrency(){
		BigDecimal price = new BigDecimal("88710.87");
		BigDecimal usdt = new BigDecimal("10.6453463");
		int scale = 6;
		RoundingMode roundingMode = RoundingMode.HALF_UP;

		BigDecimal result = usdt.divide(price, scale, roundingMode);
		System.out.println(result);

		BigDecimal bigDecimal = CurrencyConverter.convertCurrency(price, usdt, scale);
		System.out.println(bigDecimal);
	}

	@ParameterizedTest
	@CsvSource({
			// Цена, сумма в USDT, процент на увеличение, scale, ожидаемый результат
			"88710.87, 10.64, 0.01, 6, 0.000121",     // Проверка стандартного кейса
			"100000, 50, 0.05, 8, 0.000525",         // Проверка с крупной суммой
			"50000, 20, 0.1, 5, 0.00044",            // Проверка с высоким процентом
			"45000, 5, 0.01, 4, 0.0001",             // Стандартная сумма и низкий процент
			"60000, 100, 0.005, 7, 0.001675",       // Большая сумма и небольшой процент
			"45000, 10.55, 0.001, 6, 0.000235",      // Проверка с дробной суммой
			"12345.67, 1.23, 0.02, 8, 0.00010162",   // Проверка с менее крупной суммой
			"98765.43, 12.34, 0.09, 5, 0.00014",    // Средний кейс с высоким процентом
			"10000, 0.55, 0.05, 6, 0.000058",        // Проверка с очень малой суммой
			"5000, 5000, 0.01, 5, 1.01"           // Угловой случай, цена равна сумме
	})
	void testConvertCurrencyPercent(String price, String usdt, String percent, int scale, String expectedResult) {
		// Преобразуем строки в BigDecimal
		BigDecimal priceValue = new BigDecimal(price);
		BigDecimal usdtValue = new BigDecimal(usdt);
		BigDecimal percentage = new BigDecimal(percent);
		BigDecimal expectedValue = new BigDecimal(expectedResult);

		BigDecimal percentValue = FinancialCalculator.increaseByPercentage(usdtValue, percentage.doubleValue());

		// Конвертируем процент в валюту
		BigDecimal result = CurrencyConverter.convertCurrency(priceValue, percentValue, scale);


		System.out.println("USDT после увеличения на процент: " + percentValue);
		System.out.println("Результат конвертации: " + result);

		// Проверяем результат
		assertEquals(expectedValue, result);
	}


}