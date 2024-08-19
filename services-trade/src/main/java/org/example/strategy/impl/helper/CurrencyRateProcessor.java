package org.example.strategy.impl.helper;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.NodeOrder;
import org.example.websocet.WebSocketChange;
import org.knowm.xchange.dto.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class CurrencyRateProcessor {
	
	public static String messageBay(BigDecimal usd, BigDecimal price) {
		return "**[Ордер на покупку]**\n" +
				"Цена: $" + price.setScale(2, RoundingMode.HALF_UP) + "\n" +
				"Сумма ордера: $" + usd + "\n" +
				"_Статус: размещен_";
	}
	
	public static String messageSel(BigDecimal usd, BigDecimal price) {
		return "**[Ордер на продажу]**\n" +
				"Цена: $" + price.setScale(2, RoundingMode.HALF_UP) + "\n" +
				"Сумма ордера: $" + usd + "\n" +
				"_Статус: размещен_";
	}
	
	public static String messageProcessing(BigDecimal price) {
		return "_Статус: исполнен_\n" +
				"Цена: $" + price;
	}
	
	public static String messageResult(BigDecimal profit, int size) {
		return  "**[Резултат торговли]**\n" +
				"_Количество сделок: " + size + "\n" +
				"Профит: $" + profit;
	}
	
	public static BigDecimal calculateProfit(List<NodeOrder> orders) {
		BigDecimal profit = BigDecimal.ZERO;
		if (orders != null && !orders.isEmpty()) {
			for (NodeOrder order : orders) {
				if ("ASK".equalsIgnoreCase(order.getType())) {
					profit = profit.add(order.getUsd());
				} else if ("BID".equalsIgnoreCase(order.getType())) {
					profit = profit.subtract(order.getUsd());
				}
			}
		}
		return profit;
	}
	
	public static void subscribeToCurrencyRate(NodeOrder limitOrderMain, CountDownLatch latch, WebSocketChange subject) {
		if (limitOrderMain.getType().equals(Order.OrderType.BID.toString())) {
			subject.getCurrencyRateStream()
				   .filter(rate -> rate.getLimitPrice().compareTo(limitOrderMain.getLimitPrice()) < 0)
				   .filter(pair -> pair.getInstrument().equals(limitOrderMain.getInstrument()))
				   .firstElement()
				   .subscribe(
						   rate -> {
							   // Уведомляем наблюдателя о достижении курса
							   log.info("Курс достиг значения: " + rate.getLimitPrice() + " BID");
							   latch.countDown();
						   }, error -> {
							   // Обработка ошибок
							   log.error("Произошла ошибка: " + error.getMessage());
							   latch.countDown();
						   });
		}else if (limitOrderMain.getType().equals(Order.OrderType.ASK.toString())) {
			subject.getCurrencyRateStream()
				   .filter(rate -> rate.getLimitPrice().compareTo(limitOrderMain.getLimitPrice()) > 0)
				   .filter(pair -> pair.getInstrument().equals(limitOrderMain.getInstrument()))
				   .firstElement()
				   .subscribe(
						   rate -> {
							   // Уведомляем наблюдателя о достижении курса
							   log.info("Курс достиг значения: " + rate.getLimitPrice() + " ASK");
							   latch.countDown();
						   }, error -> {
							   // Обработка ошибок
							   log.error("Произошла ошибка: " + error.getMessage());
							   latch.countDown();
						   });
		} else {
			subject.getCurrencyRateStream()
				   .filter(rate -> rate.getLimitPrice().compareTo(limitOrderMain.getLimitPrice()) == 0)
				   .filter(pair -> pair.getInstrument().equals(limitOrderMain.getInstrument()))
				   .firstElement()
				   .subscribe(
						   rate -> {
							   // Уведомляем наблюдателя о достижении курса
							   log.info("Курс достиг значения: " + rate.getLimitPrice() + " ==");
							   latch.countDown();
						   }, error -> {
							   // Обработка ошибок
							   log.error("Произошла ошибка: " + error.getMessage());
							   latch.countDown();
						   });
		}
	}
}
