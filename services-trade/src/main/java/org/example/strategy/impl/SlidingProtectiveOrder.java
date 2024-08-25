package org.example.strategy.impl;

import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.StrategyEnams;
import org.example.entity.enams.TradeState;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.StrategyTrade;
import org.example.strategy.impl.helper.CurrencyRateProcessor;
import org.example.websocet.WebSocketChange;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.finance.CurrencyConverter;
import org.example.xchange.finance.FinancialCalculator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.FundsExceededException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.example.strategy.impl.helper.CreateNodeOrder.createNodeOrder;
import static org.example.strategy.impl.helper.CurrencyRateProcessor.messageResult;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlidingProtectiveOrder implements StrategyTrade {
	
	private final ProcessServiceCommand producerServiceExchange;
	private final WebSocketCommand webSocketCommand;
	private final NodeUserDAO nodeUserDAO;
	private final NodeOrdersDAO ordersDAO;
	
	public void executeTrade(NodeUser nodeUser, BasicChangeInterface basicChange) {
		NodeOrder bay = null;
		// Получение списка ордеров в стакане
		OrderBook orderBook = basicChange.orderBooksLimitOrders(nodeUser.getConfigTrade().getDepthGlass(), nodeUser.getConfigTrade().getNamePair());
		if (nodeUser.getStateTrade().equals(TradeState.BAY)) {
			bay = bay(orderBook, nodeUser, basicChange);
		}
		if (nodeUser.getStateTrade().equals(TradeState.SEL)) {
			sel(orderBook, nodeUser, bay, basicChange);
		}
		
	}
	
	public NodeOrder bay(OrderBook orderBook, NodeUser nodeUser, BasicChangeInterface basicChange) {
		List<LimitOrder> bids;
		List<BigDecimal> priceAndAmount;
		CurrencyPair currencyPair;
		BigDecimal price;
		BigDecimal amount;
		LimitOrder order;
		String orderId;
		NodeOrder nodeOrder;
		String message;
		WebSocketChange webSocketChange;
		CountDownLatch latch;
		try {
			bids = orderBook.getBids();
			currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
			price = FinancialCalculator.maxAmount(bids);
			price = CurrencyConverter.validUsd(price);
			//<!-- конвертирую  установленную сумму в валюту -->
			amount = nodeUser.getConfigTrade().getAmountOrder();
			amount = CurrencyConverter.convertCurrency(price, amount);
			//<!-- Подготавливаем лимитный ордер и отправляем его на биржу -->
			priceAndAmount = new ArrayList<>();
			priceAndAmount.add(price);
			priceAndAmount.add(amount);
			order = basicChange.createOrder(currencyPair, priceAndAmount, Order.OrderType.BID);
			orderId = basicChange.placeLimitOrder(order, nodeUser.getConfigTrade().isRealTrade());
			nodeOrder = createNodeOrder(order, orderId, priceAndAmount, nodeUser);
			nodeUser.setStateTrade(TradeState.SEL);
			//<!-- Информируем о размещении ордера -->
			message = CurrencyRateProcessor.messageBay(nodeOrder.getUsd(), nodeOrder.getLimitPrice());
			producerServiceExchange.sendAnswer(message, nodeOrder.getNodeUser().getChatId());
			webSocketChange = webSocketCommand.webSocketChange(nodeUser);
			latch = new CountDownLatch(1);
			CurrencyRateProcessor.subscribeToCurrencyRate(nodeOrder, latch, webSocketChange);
			latch.await();
			result(nodeOrder);
			return nodeOrder;
			
		} catch (FundsExceededException e) {
			producerServiceExchange.sendAnswer("Не достаточно денег на балансе", nodeUser.getChatId());
			log.error(e.getMessage());
			e.printStackTrace();
			nodeUser.setTradeStartOrStop(false);
			throw new RuntimeException();
		} catch (Exception e) {
			producerServiceExchange.sendAnswer(e.getMessage(), nodeUser.getChatId());
			log.error(e.getMessage());
			e.printStackTrace();
			nodeUser.setTradeStartOrStop(false);
			throw new RuntimeException();
		}
	}
	
	public NodeOrder sel(OrderBook orderBook, NodeUser nodeUser, NodeOrder nodeOrder, BasicChangeInterface basicChange) {
		List<LimitOrder> asks;
		List<BigDecimal> priceAndAmount;
		CurrencyPair currencyPair;
		BigDecimal price;
		BigDecimal amount;
		LimitOrder order;
		String orderId;
		NodeOrder nodeOrderResult;
		String message;
		WebSocketChange webSocketChange;
		CountDownLatch latch;
		try {
			asks = orderBook.getAsks();
			currencyPair = new CurrencyPair(nodeOrder.getInstrument());
			price = CurrencyConverter.validUsd(FinancialCalculator.maxAmount(asks));
			amount = nodeOrder.getOriginalAmount();
			priceAndAmount = new ArrayList<>();
			priceAndAmount.add(price);
			priceAndAmount.add(amount);
			order = basicChange.createOrder(currencyPair, priceAndAmount, Order.OrderType.ASK);
			orderId = basicChange.placeLimitOrder(order, nodeUser.getConfigTrade().isRealTrade());
			nodeOrderResult = createNodeOrder(order, orderId, priceAndAmount, nodeUser);
			nodeUser.setStateTrade(TradeState.BAY);
			//<!-- Информируем о размещении ордера -->
			message = CurrencyRateProcessor.messageSel(nodeOrderResult.getUsd(), nodeOrderResult.getLimitPrice());
			producerServiceExchange.sendAnswer(message, nodeOrderResult.getNodeUser().getChatId());
			webSocketChange = webSocketCommand.webSocketChange(nodeUser);
			latch = new CountDownLatch(1);
			CurrencyRateProcessor.subscribeToCurrencyRate(nodeOrderResult, latch, webSocketChange);
			latch.await();
			result(nodeOrderResult);
			nodeUser.getOrders().add(nodeOrderResult);
			nodeUserDAO.save(nodeUser);
			return nodeOrder;
		} catch (FundsExceededException e) {
			producerServiceExchange.sendAnswer("Не достаточно денег на балансе", nodeUser.getChatId());
			log.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			throw new RuntimeException();
		} catch (Exception e) {
			producerServiceExchange.sendAnswer(e.getMessage(), nodeUser.getChatId());
			log.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			throw new RuntimeException();
		}
	}
	
	public void result(NodeOrder nodeOrder) {
		//<!-- резултат трейдинга -->
		log.info(nodeOrder.getOrderId() + " : ордер был исполнен по прайсу " + nodeOrder.getLimitPrice());
		String message = CurrencyRateProcessor.messageProcessing(nodeOrder.getLimitPrice());
		producerServiceExchange.sendAnswer(message, nodeOrder.getNodeUser().getChatId());
		ordersDAO.save(nodeOrder);
	}
	
	@Override
	@Transactional
	public String trade(NodeUser nodeUser, BasicChangeInterface basicChange) {
		while (nodeUser.isTradeStartOrStop()) {
			executeTrade(nodeUser, basicChange);
			Optional<NodeUser> byId = nodeUserDAO.findById(nodeUser.getId()); //получаем обновлене состояние юзера, что бы узнать продолжать ли дальше трейдинг
			nodeUser = byId.orElse(nodeUser);
		}
		List<NodeOrder> nodeOrders = ordersDAO.findAllOrdersFromTimestampAndNodeUser(nodeUser.getLastStartTread(), nodeUser);
		producerServiceExchange.sendAnswer(messageResult(CurrencyRateProcessor.calculateProfit(nodeOrders), nodeOrders.size()), nodeUser.getChatId());
		return "";
	}
	
	@Override
	public StrategyEnams getType() {
		return StrategyEnams.SlidingProtectiveOrder;
	}
}
