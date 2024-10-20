package org.example.strategy.impl.helper;

import org.example.entity.NodeOrder;
import org.example.xchange.finance.CurrencyConverter;
import org.knowm.xchange.dto.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AssistantMessage {
    public static String messageBuy(BigDecimal usd, BigDecimal price) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Ордер на покупку]**\n")
                .append("Цена: $").append(price.setScale(2, RoundingMode.HALF_UP)).append("\n")
                .append("Сумма ордера: $").append(usd).append("\n")
                .append("_Статус: размещен_");
        return sb.toString();
    }

    public static String messageSell(BigDecimal usd, BigDecimal price) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Ордер на продажу]\n")
                .append("Цена: $").append(price.setScale(2, RoundingMode.HALF_UP)).append("\n")
                .append("Сумма ордера: $").append(usd).append("\n")
                .append("_Статус: размещен_");
        return sb.toString();
    }

    public static String messageProcessing(NodeOrder nodeOrder) {
        String bayOrSell;
        if (nodeOrder.getType().equals(Order.OrderType.ASK.toString())){
            bayOrSell = "продан";
        } else {
            bayOrSell = "куплен";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Ордер ").append(bayOrSell).append("\n")
                .append("Прайс: $").append(CurrencyConverter.validUsd(nodeOrder.getLimitPrice())).append("\n")
                .append("Сумма: $").append(nodeOrder.getUsd());
        return sb.toString();
    }

    public static String messageResult(BigDecimal profit, int size) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Резултат торговли]**\n")
                .append("_Количество сделок: ").append(size).append("\n")
                .append("Профит: $").append(profit);
        return sb.toString();
    }

    public static String messageCancel(String orderId) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Ордер Id: ").append(orderId).append("]\n")
                .append("Был отменен ");
        return sb.toString();
    }

}
