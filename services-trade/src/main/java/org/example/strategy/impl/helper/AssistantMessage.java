package org.example.strategy.impl.helper;

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
	
	public static String messageProcessing(BigDecimal price) {
		StringBuilder sb = new StringBuilder();
		sb.append("_Статус: исполнен_\n")
		  .append("Цена: $").append(price);
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
