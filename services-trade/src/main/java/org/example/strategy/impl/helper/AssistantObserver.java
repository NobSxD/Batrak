package org.example.strategy.impl.helper;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.NodeOrder;
import org.example.websocet.WebSocketChange;
import org.knowm.xchange.dto.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.example.entity.enams.state.OrderState.FILLED;

@Slf4j
public class AssistantObserver {

    public static BigDecimal calculateProfit(List<NodeOrder> orders) {
        BigDecimal profit = BigDecimal.ZERO;

        if (orders != null && !orders.isEmpty()) {
            for (NodeOrder order : orders) {
                BigDecimal orderValue = order.getUsd();

                if (orderValue == null && !order.getOrderState().equals(FILLED)) {
                    continue; // пропускаем ордера со значением null
                }

                switch (order.getType().toUpperCase()) {
                    case "ASK" -> profit = profit.add(orderValue);
                    case "BID" -> profit = profit.subtract(orderValue);
                }
            }
        }

        return profit;
    }

    public static void subscribeToCurrencyRate(NodeOrder limitOrderMain, CountDownLatch latch, WebSocketChange subject,
                                               Order.OrderType orderType, TradeStatusManager tradeStatusManager) {
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
                                tradeStatusManager.sell();
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
                                tradeStatusManager.buy();
                            }, error -> {
                                // Обработка ошибок
                                log.error("Произошла ошибка: {}", error.getMessage());
                                latch.countDown();
                            });
        }

    }

    public static void subscribeToCurrencyRateForGridTrading(NodeOrder limitOrderMain, CountDownLatch latch, WebSocketChange subject,
                                                             Order.OrderType orderType, TradeStatusManager tradeStatusManager, List<BigDecimal> gridLevels) {

        // Сначала определим направление торгового действия
        if (orderType.equals(Order.OrderType.BID)) {
            subject.getCurrencyRateStream()
                    .filter(pair -> pair.getInstrument().equals(limitOrderMain.getInstrument()))
                    .subscribe(
                            rate -> {
                                // Проходим по каждой сетке уровней
                                for (BigDecimal gridLevel : gridLevels) {
                                    if (rate.getLimitPrice().compareTo(gridLevel) < 0) {
                                        log.info("Курс достиг значения: {} (Grid Level) BID", rate.getLimitPrice());
                                        latch.countDown();
                                        tradeStatusManager.sell();
                                        break; // Останавливаем проверку, если один ордер срабатывает
                                    }
                                }
                            }, error -> {
                                log.error("Произошла ошибка: {}", error.getMessage());
                                latch.countDown();
                            });
        } else if (orderType.equals(Order.OrderType.ASK)) {
            subject.getCurrencyRateStream()
                    .filter(pair -> pair.getInstrument().equals(limitOrderMain.getInstrument()))
                    .subscribe(
                            rate -> {
                                for (BigDecimal gridLevel : gridLevels) {
                                    if (rate.getLimitPrice().compareTo(gridLevel) > 0) {
                                        log.info("Курс достиг значения: {} (Grid Level) ASK", rate.getLimitPrice());
                                        latch.countDown();
                                        tradeStatusManager.buy();
                                        break;
                                    }
                                }
                            }, error -> {
                                log.error("Произошла ошибка: {}", error.getMessage());
                                latch.countDown();
                            });
        }
    }
}
