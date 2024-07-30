package org.example.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.dao.StatisticsTradeDAO;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.Statistics;
import org.example.entity.enams.OrderType;
import org.example.entity.enams.StrategyEnams;
import org.example.entity.enams.TradeState;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.StrategyTrade;
import org.example.strategy.impl.helper.CurrencyRateProcessor;
import org.example.websocet.WebSocketBinanceChange;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.DTO.LimitOrderMain;
import org.example.xchange.finance.CurrencyConverter;
import org.example.xchange.finance.FinancialCalculator;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.FundsExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
@RequiredArgsConstructor
public class SlidingProtectiveOrder implements StrategyTrade {
	private static final Logger logger = LoggerFactory.getLogger(SlidingProtectiveOrder.class);
	//private BasicChangeInterface basicChange;
	private final ProcessServiceCommand producerServiceExchange;
	private final NodeUserDAO nodeUserDAO;
	private final NodeOrdersDAO ordersDAO;
	private final StatisticsTradeDAO statisticsTradeDAO;

	@Autowired
	private WebSocketBinanceChange subject;


	public void executeTrade(NodeUser nodeUser, BasicChangeInterface basicChange) {
		//this.basicChange = basicChange;
		NodeOrder bay = null;
		// Получение списка ордеров в стакане
		OrderBook orderBook = basicChange.orderBooksLimitOrders(nodeUser.getConfigTrade().getDepthGlass(), nodeUser.getConfigTrade().getNamePair());
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
			NodeOrder nodeOrder = general(nodeUser, bids, Order.OrderType.BID, basicChange);
			nodeUser.setStateTrade(TradeState.SEL);
			return nodeOrder;

		} catch (FundsExceededException e) {
			producerServiceExchange.sendAnswer("Не достаточно денег на балансе", nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			return null;
		} catch (Exception e) {
			producerServiceExchange.sendAnswer(e.getMessage(), nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			return null;
		}
	}

	public NodeOrder sel(OrderBook orderBook, NodeUser nodeUser, NodeOrder nodeOrder, BasicChangeInterface basicChange) {
		try {
			List<LimitOrder> asks = orderBook.getAsks();
			NodeOrder resulOrder = general(nodeUser, asks, Order.OrderType.ASK, basicChange);
			nodeUser.setStateTrade(TradeState.BAY);
			return resulOrder;
		} catch (FundsExceededException e) {
			producerServiceExchange.sendAnswer("Не достаточно денег на балансе", nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			return null;
		} catch (Exception e) {
			producerServiceExchange.sendAnswer(e.getMessage(), nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			return null;
		}
	}

	public NodeOrder general(NodeUser nodeUser, List<LimitOrder> bidsOrAsk, Order.OrderType orderType,
						BasicChangeInterface basicChange) throws InterruptedException {

		CurrencyPair currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
		BigDecimal price = FinancialCalculator.maxAmount(bidsOrAsk);
		price = CurrencyConverter.validUsd(price);

		//<!-- конвертирую  установленную сумму в валюту -->
		BigDecimal amount = nodeUser.getConfigTrade().getAmountOrder();
		amount = CurrencyConverter.convertCurrency(price, amount);

		//<!-- Подготавливаем лимитный ордер и отправляем его на биржу -->
		List<BigDecimal> priceAndAmount = new ArrayList<>();
		priceAndAmount.add(price);
		priceAndAmount.add(amount);
		LimitOrder order = basicChange.createOrder(currencyPair, priceAndAmount, orderType);

		//TODO вынести в настройки трейдинг на вертуальный счет
		LimitOrderMain limitOrderMain = basicChange.placeLimitOrder(order, true);
		BigDecimal usd = CurrencyConverter.convertUsdt(amount, price);
		String message = "";
		if (limitOrderMain.getOrderMain().getType().equals(Order.OrderType.BID)){
			message = CurrencyRateProcessor.messageBay(usd, price);
		} else {
			message = CurrencyRateProcessor.messageSel(usd, price);
		}

		logger.info(message + "user_id: " + nodeUser.getId() + "user_chat_id " + nodeUser.getChatId());
		//<!-- Информируем о размещении ордера -->
		producerServiceExchange.sendAnswer(message, nodeUser.getChatId());
		CountDownLatch latch = new CountDownLatch(1);
		CurrencyRateProcessor.subscribeToCurrencyRate(limitOrderMain, latch, subject);
		latch.await();
		logger.info("ордер исполнился");
		return resultTread(nodeUser, limitOrderMain, priceAndAmount);
	}



	public NodeOrder resultTread(NodeUser nodeUser, LimitOrderMain limitOrderMain, List<BigDecimal> priceAndAmount) {
		BigDecimal price = priceAndAmount.get(0);
		BigDecimal amount = priceAndAmount.get(1);

		BigDecimal usd = CurrencyConverter.convertUsdt(amount, price);



		OrderType orderType = null;
		if (limitOrderMain.getOrderMain().getType().equals(Order.OrderType.BID)){
			orderType = OrderType.BIDS;
		} else if (limitOrderMain.getOrderMain().getType().equals(Order.OrderType.ASK)) {
			orderType = OrderType.ASKS;
		}

		LocalDateTime date;
		if (limitOrderMain.getOrderMain().getTimestamp() != null){
			date = limitOrderMain.getOrderMain().getTimestamp();

		}else {
			date = LocalDateTime.now();
		}

		final NodeOrder nodeOrderResult = NodeOrder.builder()
				.price(price)
				.orderId(limitOrderMain.getOrderMain().getId())
				.originalAmount(limitOrderMain.getOrderMain().getOriginalAmount())
				.summaUSD(usd)
				.orderType(orderType)
				.pair(nodeUser.getConfigTrade().getNamePair())
				.createDate(date)
				.strategyEnams(nodeUser.getConfigTrade().getStrategy())
				.nodeUser(nodeUser)
				.build();

		logger.info(limitOrderMain.getOrderMain().getId() + " : ордер был исполнен по прайсу " + limitOrderMain.getLimitPrice());
		String message = CurrencyRateProcessor.messageProcessing(price);
		producerServiceExchange.sendAnswer(message, nodeUser.getChatId());
		ordersDAO.save(nodeOrderResult);
		nodeUser.getOrders().add(nodeOrderResult);
		nodeUserDAO.save(nodeUser);
		return nodeOrderResult;
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
