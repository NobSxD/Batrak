package org.example.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.dao.StatisticsTradeDAO;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.Statistics;
import org.example.entity.enams.StrategyEnams;
import org.example.entity.enams.TradeState;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.StrategyTrade;
import org.example.strategy.impl.helper.CurrencyRateProcessor;
import org.example.websocet.WebSocketBinanceChange;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.finance.CurrencyConverter;
import org.example.xchange.finance.FinancialCalculator;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.FundsExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlidingProtectiveOrder implements StrategyTrade {
	
	private final ProcessServiceCommand producerServiceExchange;
	private final NodeUserDAO nodeUserDAO;
	private final NodeOrdersDAO ordersDAO;
	private final StatisticsTradeDAO statisticsTradeDAO;
	
	@Autowired
	private WebSocketBinanceChange subject;
	
	public void executeTrade(NodeUser nodeUser, BasicChangeInterface basicChange) {
		NodeOrder bay = null;
		// Получение списка ордеров в стакане
		OrderBook orderBook = basicChange.orderBooksLimitOrders(nodeUser.getConfigTrade()
																		.getDepthGlass(), nodeUser.getConfigTrade()
																								  .getNamePair());
		Statistics statistics = nodeUser.getStatistics();
		if (nodeUser.getStateTrade().equals(TradeState.BAY)) {
			bay = bay(orderBook, nodeUser, basicChange);
		}
		if (nodeUser.getStateTrade().equals(TradeState.SEL)) {
			sel(orderBook, nodeUser, bay, basicChange);
		}
		
	}
	
	public NodeOrder bay(OrderBook orderBook, NodeUser nodeUser, BasicChangeInterface basicChange) {
		try {
			List<LimitOrder> bids = orderBook.getBids();
			
			CurrencyPair currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
			BigDecimal price = FinancialCalculator.maxAmount(bids);
			price = CurrencyConverter.validUsd(price);
			
			//<!-- конвертирую  установленную сумму в валюту -->
			BigDecimal amount = nodeUser.getConfigTrade().getAmountOrder();
			amount = CurrencyConverter.convertCurrency(price, amount);
			
			//<!-- Подготавливаем лимитный ордер и отправляем его на биржу -->
			List<BigDecimal> priceAndAmount = new ArrayList<>();
			priceAndAmount.add(price);
			priceAndAmount.add(amount);
			LimitOrder order = basicChange.createOrder(currencyPair, priceAndAmount, Order.OrderType.BID);
			
			String orderId = basicChange.placeLimitOrder(order, nodeUser.getConfigTrade().isRealTrade());
			NodeOrder nodeOrder = createNodeOrder(order, orderId, priceAndAmount, nodeUser);
			
			nodeUser.setStateTrade(TradeState.SEL);
			general(nodeOrder);
			return nodeOrder;
			
		} catch (FundsExceededException e) {
			producerServiceExchange.sendAnswer("Не достаточно денег на балансе", nodeUser.getChatId());
			log.error(e.getMessage());
			e.printStackTrace();
			nodeUser.setTradeStartOrStop(false);
			return null;
		} catch (Exception e) {
			producerServiceExchange.sendAnswer(e.getMessage(), nodeUser.getChatId());
			log.error(e.getMessage());
			e.printStackTrace();
			nodeUser.setTradeStartOrStop(false);
			return null;
		}
	}
	
	public NodeOrder sel(OrderBook orderBook, NodeUser nodeUser, NodeOrder nodeOrder, BasicChangeInterface basicChange) {
		try {
			List<LimitOrder> asks = orderBook.getAsks();
			CurrencyPair currencyPair = new CurrencyPair(nodeOrder.getInstrument());
			
			BigDecimal price = CurrencyConverter.validUsd(FinancialCalculator.maxAmount(asks));
			BigDecimal amount = nodeOrder.getOriginalAmount();
			
			List<BigDecimal> priceAndAmount = new ArrayList<>();
			priceAndAmount.add(price);
			priceAndAmount.add(amount);
			
			LimitOrder order = basicChange.createOrder(currencyPair, priceAndAmount, Order.OrderType.ASK);
			String orderId = basicChange.placeLimitOrder(order, nodeUser.getConfigTrade().isRealTrade());
			NodeOrder nodeOrderResult = createNodeOrder(order, orderId, priceAndAmount, nodeUser);
			
			nodeUser.setStateTrade(TradeState.BAY);
			general(nodeOrderResult);
			nodeUser.getOrders().add(nodeOrderResult);
			nodeUserDAO.save(nodeUser);
			return nodeOrder;
		} catch (FundsExceededException e) {
			producerServiceExchange.sendAnswer("Не достаточно денег на балансе", nodeUser.getChatId());
			log.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			return null;
		} catch (Exception e) {
			producerServiceExchange.sendAnswer(e.getMessage(), nodeUser.getChatId());
			log.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			return null;
		}
	}
	
	public void general(NodeOrder nodeOrder) throws InterruptedException {
		
		String message = "";
		if (nodeOrder.getType().equals(Order.OrderType.BID.toString())) {
			message = CurrencyRateProcessor.messageBay(nodeOrder.getUsd(), nodeOrder.getLimitPrice());
		} else {
			message = CurrencyRateProcessor.messageSel(nodeOrder.getUsd(), nodeOrder.getLimitPrice());
		}
		
		log.info(message + "user_id: " + nodeOrder.getNodeUser().getId() + "user_chat_id " + nodeOrder.getNodeUser()
																									  .getChatId());
		//<!-- Информируем о размещении ордера -->
		producerServiceExchange.sendAnswer(message, nodeOrder.getNodeUser().getChatId());
		CountDownLatch latch = new CountDownLatch(1);
		CurrencyRateProcessor.subscribeToCurrencyRate(nodeOrder, latch, subject);
		latch.await();
		log.info("ордер исполнился");
		
		//<!-- резултат трейдинга -->
		log.info(nodeOrder.getOrderId() + " : ордер был исполнен по прайсу " + nodeOrder.getLimitPrice());
		message = CurrencyRateProcessor.messageProcessing(nodeOrder.getLimitPrice());
		producerServiceExchange.sendAnswer(message, nodeOrder.getNodeUser().getChatId());
		log.info(nodeOrder.getOriginalAmount().toString());
		ordersDAO.save(nodeOrder);
	}
	
	private NodeOrder createNodeOrder(LimitOrder limitOrder, String orderId, List<BigDecimal> priceAndAmount, NodeUser nodeUser) {
		BigDecimal usd = CurrencyConverter.convertUsdt(priceAndAmount.get(1), priceAndAmount.get(0));
		return NodeOrder.builder()
						.type(limitOrder.getType().name())
						.orderId(orderId)
						.originalAmount(limitOrder.getOriginalAmount())
						.limitPrice(limitOrder.getLimitPrice())
						.cumulativeAmount(limitOrder.getCumulativeAmount())
						.averagePrice(limitOrder.getAveragePrice())
						.usd(usd)
						.instrument(limitOrder.getInstrument().toString())
						.timestamp(limitOrder.getTimestamp())
						.userReference(limitOrder.getUserReference())
						.checkReal(nodeUser.getConfigTrade().isRealTrade())
						.strategyEnams(nodeUser.getConfigTrade().getStrategy())
						.nodeUser(nodeUser)
						.build();
	}
	
	@Override
	public String trade(NodeUser nodeUser, BasicChangeInterface basicChange) {
		while (nodeUser.isTradeStartOrStop()) {
			executeTrade(nodeUser, basicChange);
		}
		return "Торговля началась";
	}
	
	@Override
	public StrategyEnams getType() {
		return StrategyEnams.SlidingProtectiveOrder;
	}
}
