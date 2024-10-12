package org.example.strategy.impl.helper;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.NodeOrder;

import java.math.BigDecimal;
import java.util.List;

import static org.example.entity.enams.state.OrderState.COMPLETED;

@Slf4j
public class AssistantObserver {

    public static BigDecimal calculateProfit(List<NodeOrder> orders) {
        BigDecimal profit = BigDecimal.ZERO;

        if (orders != null && !orders.isEmpty()) {
            for (NodeOrder order : orders) {
                BigDecimal orderValue = order.getUsd();

                if (orderValue == null && !order.getOrderState().equals(COMPLETED)) {
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

}
