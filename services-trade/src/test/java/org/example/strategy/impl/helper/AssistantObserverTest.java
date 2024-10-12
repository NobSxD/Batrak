package org.example.strategy.impl.helper;

import org.example.entity.NodeOrder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssistantObserverTest {
	
	@Test
	public void testCalculateProfit_MixedOrders() {
		
		List<NodeOrder> orders = Arrays.asList(
				NodeOrder.builder().usd(new BigDecimal("200.00")).type("ASK").build(),
				NodeOrder.builder().usd(new BigDecimal("150.00")).type("BID").build(),
				NodeOrder.builder().usd(new BigDecimal("100.00")).type("ASK").build(),
				NodeOrder.builder().usd(new BigDecimal("50.00")).type("BID").build()
		);
		BigDecimal profit = AssistantObserver.calculateProfit(orders);
		assertEquals(new BigDecimal("100.00"), profit);
	}
	
	@Test
	public void testCalculateProfit_AllAskOrders() {
		List<NodeOrder> orders = Arrays.asList(
				NodeOrder.builder().usd(new BigDecimal("200.00")).type("ASK").build(),
				NodeOrder.builder().usd(new BigDecimal("100.00")).type("ASK").build()
		);
		
		BigDecimal profit = AssistantObserver.calculateProfit(orders);
		assertEquals(new BigDecimal("300.00"), profit);
	}
	
	@Test
	public void testCalculateProfit_AllBidOrders() {
		List<NodeOrder> orders = Arrays.asList(
				NodeOrder.builder().usd(new BigDecimal("200.00")).type("BID").build(),
				NodeOrder.builder().usd(new BigDecimal("100.00")).type("BID").build()
		);
		BigDecimal profit = AssistantObserver.calculateProfit(orders);
		assertEquals(new BigDecimal("-300.00"), profit);
	}
	
	@Test
	public void testCalculateProfit_EmptyOrders() {
		List<NodeOrder> orders = Arrays.asList();
		BigDecimal profit = AssistantObserver.calculateProfit(orders);
		assertEquals(BigDecimal.ZERO, profit);
	}
	
	@Test
	public void testCalculateProfit_NullOrders() {
		BigDecimal profit = AssistantObserver.calculateProfit(null);
		assertEquals(BigDecimal.ZERO, profit);
	}

}
