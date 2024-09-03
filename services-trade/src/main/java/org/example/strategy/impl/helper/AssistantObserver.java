package org.example.strategy.impl.helper;

import org.example.entity.NodeOrder;
import org.example.entity.enams.state.OrderState;
import org.example.websocet.WebSocketChange;

import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.dto.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class AssistantObserver {
	
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
	
	public static void subscribeToCurrencyRate(NodeOrder limitOrderMain, CountDownLatch latch, WebSocketChange subject, Observable<NodeOrder> cancelEventStream) {
		if (limitOrderMain.getType().equals(Order.OrderType.BID.toString())) {
			subject.getCurrencyRateStream()
				   .filter(rate -> rate.getLimitPrice().compareTo(limitOrderMain.getLimitPrice()) < 0)
				   .filter(pair -> pair.getInstrument().equals(limitOrderMain.getInstrument()))
				   .firstElement()
				   .subscribe(
						   rate -> {
							   // Уведомляем наблюдателя о достижении курса
							   log.info("Курс достиг значения: {} BID", rate.getLimitPrice());
							   latch.countDown();
						   }, error -> {
							   // Обработка ошибок
							   log.error("Произошла ошибка: {}", error.getMessage());
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
							   log.info("Курс достиг значения: {} ASK",  rate.getLimitPrice());
							   latch.countDown();
						   }, error -> {
							   // Обработка ошибок
							   log.error("Произошла ошибка: {}", error.getMessage());
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
							   log.info("Курс достиг значения: {}",  rate.getLimitPrice());
							   latch.countDown();
						   }, error -> {
							   // Обработка ошибок
							   log.error("Произошла ошибка: {}", error.getMessage());
							   latch.countDown();
						   });
		}
		cancelEventStream
				.filter(nodeOrder -> nodeOrder.getOrderState().equals(OrderState.PENDING_CANCEL))
				.firstElement()
				.subscribe(
						event -> {
							// Обработка события отмены
							log.info("Торговля отменена.");
							latch.countDown();
						},
						error -> {
							// Обработка ошибок
							log.error("Произошла ошибка при отмене торговли: {}",  error.getMessage());
							latch.countDown();
						});
	}
}
