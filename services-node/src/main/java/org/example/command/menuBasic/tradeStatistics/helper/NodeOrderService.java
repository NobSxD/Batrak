package org.example.command.menuBasic.tradeStatistics.helper;

import java.math.BigDecimal;
import java.util.List;

import org.example.entity.NodeOrder;

public class NodeOrderService {

    public static BigDecimal totalBidUsd(List<NodeOrder> nodeOrders) {
        return nodeOrders.stream()
                .filter(order -> "BID".equals(order.getType()))
                .map(NodeOrder::getUsd)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal totalAskUsd(List<NodeOrder> nodeOrders) {
        return nodeOrders.stream()
                .filter(order -> "ASK".equals(order.getType()))
                .map(NodeOrder::getUsd)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static long countBid(List<NodeOrder> nodeOrders) {
        return nodeOrders.stream()
                .filter(order -> "BID".equals(order.getType()))
                .count();
    }

    public static long countAsk(List<NodeOrder> nodeOrders) {
        return nodeOrders.stream()
                .filter(order -> "ASK".equals(order.getType()))
                .count();
    }

    public static BigDecimal difference(BigDecimal totalAskUsd, BigDecimal totalBidUsd) {
        return totalAskUsd.subtract(totalBidUsd);
    }

    public static String messageStatistics(List<NodeOrder> nodeOrders, String message) {
        BigDecimal totalBidUsd = NodeOrderService.totalBidUsd(nodeOrders);

        BigDecimal totalAskUsd = NodeOrderService.totalAskUsd(nodeOrders);

        long countBid = NodeOrderService.countBid(nodeOrders);

        long countAsk = NodeOrderService.countAsk(nodeOrders);

        BigDecimal difference = NodeOrderService.difference(totalAskUsd, totalBidUsd);

        return STATE_INFO(message, totalBidUsd, totalAskUsd, countBid, countAsk, difference);
    }

    public static String STATE_INFO(String message, BigDecimal totalBidUsd, BigDecimal totalAskUsd, long countBid, long countAsk, BigDecimal difference) {
        return """
                %s :
                Всего купленно на $ : %s
                Всего проданно на $ : %s
                Всего ордеров на покупку : %s
                Всего ордеров на продажу : %s
                Прибыль $ : %s""".formatted(message, totalBidUsd, totalAskUsd, countBid, countAsk, difference);
    }
}
