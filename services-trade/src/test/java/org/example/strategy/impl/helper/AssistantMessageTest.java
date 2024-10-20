package org.example.strategy.impl.helper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssistantMessageTest {
	
	private static BigDecimal usd;
	private static BigDecimal price;
	private static BigDecimal profit;
	private static int size;
	
	@BeforeAll
	public static void setUp() {
		usd = new BigDecimal("1000");
		price = new BigDecimal("25000.999");
		profit = new BigDecimal("12345.67");
		size = 5;
	}
	
	@Test
	public void testOldAndNewMethodsPerformance() {
		// Measuring performance of old methods
		long startOldBuy = System.nanoTime();
		String oldBuyMessage = messageBuyOld(usd, price);
		long endOldBuy = System.nanoTime();
		long durationOldBuy = TimeUnit.NANOSECONDS.toMicros(endOldBuy - startOldBuy);
		
		long startOldSell = System.nanoTime();
		String oldSellMessage = messageSellOld(usd, price);
		long endOldSell = System.nanoTime();
		long durationOldSell = TimeUnit.NANOSECONDS.toMicros(endOldSell - startOldSell);
		
		long startOldProcessing = System.nanoTime();
		String oldProcessingMessage = messageProcessingOld(price);
		long endOldProcessing = System.nanoTime();
		long durationOldProcessing = TimeUnit.NANOSECONDS.toMicros(endOldProcessing - startOldProcessing);
		
		long startOldResult = System.nanoTime();
		String oldResultMessage = messageResultOld(profit, size);
		long endOldResult = System.nanoTime();
		long durationOldResult = TimeUnit.NANOSECONDS.toMicros(endOldResult - startOldResult);
		
		// Measuring performance of new methods
		long startNewBuy = System.nanoTime();
		String newBuyMessage = AssistantMessage.messageBuy(usd, price);
		long endNewBuy = System.nanoTime();
		long durationNewBuy = TimeUnit.NANOSECONDS.toMicros(endNewBuy - startNewBuy);
		
		long startNewSell = System.nanoTime();
		String newSellMessage = AssistantMessage.messageSell(usd, price);
		long endNewSell = System.nanoTime();
		long durationNewSell = TimeUnit.NANOSECONDS.toMicros(endNewSell - startNewSell);

		
		long startNewResult = System.nanoTime();
		String newResultMessage = AssistantMessage.messageResult(profit, size);
		long endNewResult = System.nanoTime();
		long durationNewResult = TimeUnit.NANOSECONDS.toMicros(endNewResult - startNewResult);
		
		// Asserting the equality of results
		assertEquals(oldBuyMessage, newBuyMessage);
		assertEquals(oldSellMessage, newSellMessage);
		assertEquals(oldResultMessage, newResultMessage);
		
		// Printing durations
		System.out.println("Old Buy Duration: " + durationOldBuy + " micros");
		System.out.println("New Buy Duration: " + durationNewBuy + " micros");
		
		System.out.println("Old Sell Duration: " + durationOldSell + " micros");
		System.out.println("New Sell Duration: " + durationNewSell + " micros");
		
		System.out.println("Old Processing Duration: " + durationOldProcessing + " micros");
		
		System.out.println("Old Result Duration: " + durationOldResult + " micros");
		System.out.println("New Result Duration: " + durationNewResult + " micros");
	}
	
	// Old methods for comparison purposes
	public static String messageBuyOld(BigDecimal usd, BigDecimal price) {
		return "[Ордер на покупку]**\n" +
				"Цена: $" + price.setScale(2, RoundingMode.HALF_UP) + "\n" +
				"Сумма ордера: $" + usd + "\n" +
				"_Статус: размещен_";
	}
	
	public static String messageSellOld(BigDecimal usd, BigDecimal price) {
		return "[Ордер на продажу]\n" +
				"Цена: $" + price.setScale(2, RoundingMode.HALF_UP) + "\n" +
				"Сумма ордера: $" + usd + "\n" +
				"_Статус: размещен_";
	}
	
	public static String messageProcessingOld(BigDecimal price) {
		return "_Статус: исполнен_\n" +
				"Цена: $" + price;
	}
	
	public static String messageResultOld(BigDecimal profit, int size) {
		return "[Резултат торговли]**\n" +
				"_Количество сделок: " + size + "\n" +
				"Профит: $" + profit;
	}
}