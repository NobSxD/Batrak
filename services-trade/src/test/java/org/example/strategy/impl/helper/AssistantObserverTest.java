package org.example.strategy.impl.helper;

import org.example.entity.NodeOrder;
import org.example.entity.enams.state.OrderState;
import org.example.websocet.WebSocketChange;

import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.dto.Order;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
	
	@Test
	public void testCurrencyRateReached() throws InterruptedException {
		NodeOrder mockOrder = mock(NodeOrder.class);
		when(mockOrder.getType()).thenReturn(Order.OrderType.BID.toString());
		when(mockOrder.getLimitPrice()).thenReturn(new BigDecimal("50000.0"));
		when(mockOrder.getInstrument()).thenReturn("BTC-USDT");
		
		WebSocketChange mockSubject = mock(WebSocketChange.class);
		Observable<NodeOrder> rateStream = Observable.just(NodeOrder.builder()
																	.limitPrice(new BigDecimal("49000.0"))
																	.instrument("BTC-USDT")
																	.originalAmount(new BigDecimal("0.9"))
																	.build());
		when(mockSubject.getCurrencyRateStream()).thenReturn(rateStream);
		CountDownLatch latch = new CountDownLatch(1);
		AssistantObserver.subscribeToCurrencyRate(mockOrder, latch, mockSubject, Observable.never());
		assertTrue(latch.await(1, TimeUnit.SECONDS), "Latch was not counted down when rate was reached.");
	}
	
	@Test
	public void testTradeCancelled() throws InterruptedException {
		NodeOrder mockOrder = mock(NodeOrder.class);
		when(mockOrder.getType()).thenReturn(Order.OrderType.BID.toString());
		
		WebSocketChange mockSubject = mock(WebSocketChange.class);
		when(mockSubject.getCurrencyRateStream()).thenReturn(Observable.never());
		
		CountDownLatch latch = new CountDownLatch(1);
		Observable<NodeOrder> cancelEventStream = Observable.just(NodeOrder.builder()
																		   .limitPrice(new BigDecimal("50000"))
																		   .instrument("BTC-USDT")
																		   .originalAmount(new BigDecimal("0.9"))
																		   .orderState(OrderState.PENDING_CANCEL)
																		   .build());
		AssistantObserver.subscribeToCurrencyRate(mockOrder, latch, mockSubject, cancelEventStream);
		assertTrue(latch.await(1, TimeUnit.SECONDS), "Latch was not counted down when trade was cancelled.");
	}
}
