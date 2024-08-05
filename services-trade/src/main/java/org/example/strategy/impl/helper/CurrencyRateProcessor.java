package org.example.strategy.impl.helper;

import org.example.websocet.WebSocketCommand;
import org.example.xchange.DTO.LimitOrderMain;
import org.knowm.xchange.dto.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;

public class CurrencyRateProcessor {
	public static String messageBay(BigDecimal usd, BigDecimal price){
		return  "**[Ордер на покупку]**\n" +
				"Цена: $" + price.setScale(2, RoundingMode.HALF_UP) + "\n" +
				"Сумма ордера: $" + usd + "\n" +
				"_Статус: размещен_";
	}
	public static String messageSel(BigDecimal usd, BigDecimal price){
		return  "**[Ордер на продажу]**\n" +
				"Цена: $" + price.setScale(2, RoundingMode.HALF_UP) + "\n" +
				"Сумма ордера: $" + usd + "\n" +
				"_Статус: размещен_";
	}
	public static String messageProcessing(BigDecimal price){
		return  "**[Выполнение ордера]**\n" +
				"Цена: $" + price + "\n" +
				"_Статус: исполнен_";
	}

	public static void subscribeToCurrencyRate(LimitOrderMain limitOrderMain, CountDownLatch latch, WebSocketCommand subject) {
		if (limitOrderMain.getOrderMain().getType().equals(Order.OrderType.BID)){
			subject.getCurrencyRateStream()
					.filter(rate -> rate.getLimitPrice().compareTo(limitOrderMain.getLimitPrice()) < 0)
					.filter(pair -> pair.getOrderMain().getInstrument().equals(limitOrderMain.getOrderMain().getInstrument()))
					.firstElement()
					.subscribe(
							rate -> {
								// Уведомляем наблюдателя о достижении курса
								System.out.println("Курс достиг значения: " + rate);
								latch.countDown();
							}, error -> {
								// Обработка ошибок
								System.err.println("Произошла ошибка: " + error.getMessage());
								latch.countDown();
							});
			return;
		}
		if (limitOrderMain.getOrderMain().getType().equals(Order.OrderType.ASK)) {
			subject.getCurrencyRateStream()
					.filter(rate -> rate.getLimitPrice().compareTo(limitOrderMain.getLimitPrice()) > 0)
					.filter(pair -> pair.getOrderMain().getInstrument().equals(limitOrderMain.getOrderMain().getInstrument()))
					.firstElement()
					.subscribe(
							rate -> {
								// Уведомляем наблюдателя о достижении курса
								System.out.println("Курс достиг значения: " + rate);
								latch.countDown();
							}, error -> {
								// Обработка ошибок
								System.err.println("Произошла ошибка: " + error.getMessage());
								latch.countDown();
							});
		}else {
			subject.getCurrencyRateStream()
					.filter(rate -> rate.getLimitPrice().compareTo(limitOrderMain.getLimitPrice()) == 0)
					.firstElement()
					.subscribe(
							rate -> {
								// Уведомляем наблюдателя о достижении курса
								System.out.println("Курс достиг значения: " + rate);
								latch.countDown();
							}, error -> {
								// Обработка ошибок
								System.err.println("Произошла ошибка: " + error.getMessage());
								latch.countDown();
							});
		}
	}
}
