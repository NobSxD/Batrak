package org.example.strategy.impl.helper;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.NodeOrder;
import org.example.websocet.WebSocketChange;
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

    public static void subscribeToCurrencyRate(NodeOrder limitOrderMain, CountDownLatch latch, WebSocketChange subject, Order.OrderType orderType) {
        if (orderType.equals(Order.OrderType.BID)) {
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
        } else if (orderType.equals(Order.OrderType.ASK)) {
            subject.getCurrencyRateStream()
                    .filter(rate -> rate.getLimitPrice().compareTo(limitOrderMain.getLimitPrice()) > 0)
                    .filter(pair -> pair.getInstrument().equals(limitOrderMain.getInstrument()))
                    .firstElement()
                    .subscribe(
                            rate -> {
                                // Уведомляем наблюдателя о достижении курса
                                log.info("Курс достиг значения: {} ASK", rate.getLimitPrice());
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
                                log.info("Курс достиг значения: {}", rate.getLimitPrice());
                                latch.countDown();
                            }, error -> {
                                // Обработка ошибок
                                log.error("Произошла ошибка: {}", error.getMessage());
                                latch.countDown();
                            });
        }

    }
}
