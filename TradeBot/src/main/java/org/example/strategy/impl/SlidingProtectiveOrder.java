package org.example.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
@RequiredArgsConstructor
public class SlidingProtectiveOrder implements StrategyTrade {
	private static final Logger logger = Logger.getLogger(SlidingProtectiveOrder.class);
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
		OrderBook orderBook = basicChange.orderBooksLimitOrders(nodeUser.getConfigTrade().getDepthGlass(), nodeUser);
		Statistics statistics = nodeUser.getStatistics();
		if (nodeUser.getStateTrade().equals(TradeState.BAY)) {
			bay = bay(orderBook, nodeUser, basicChange);
		}
		if (nodeUser.getStateTrade().equals(TradeState.SEL)) {
			sel(orderBook, nodeUser, bay, basicChange);
		}


	}
	public NodeOrder general(NodeUser nodeUser, List<LimitOrder> bidsOrAsk, Order.OrderType orderType,
						BasicChangeInterface basicChange, String message) throws InterruptedException {
		CurrencyPair currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
		BigDecimal price = FinancialCalculator.maxAmount(bidsOrAsk);
		price = CurrencyConverter.validUsd(price);

		BigDecimal amount = nodeUser.getConfigTrade().getAmountOrder();
		amount = CurrencyConverter.convertCurrency(price, amount);

		List<BigDecimal> priceAndAmount = new ArrayList<>();
		priceAndAmount.add(price);
		priceAndAmount.add(amount);
		LimitOrder order = basicChange.createOrder(currencyPair, priceAndAmount, orderType);
		//TODO вынести в настройки трейдинг на вертуальный счет
		LimitOrderMain limitOrderMain = basicChange.placeLimitOrder(order, true);
		BigDecimal usd = CurrencyConverter.convertUsdt(amount, price);
		logger.info(message + "user_id: " + nodeUser.getId() + "user_chat_id " + nodeUser.getChatId());
		producerServiceExchange.sendAnswer(message, nodeUser.getChatId());
		CountDownLatch latch = new CountDownLatch(1);
		subscribeToCurrencyRate(limitOrderMain, latch);
		latch.await();
		logger.info("ордер исполнился");
		return resultTread(nodeUser, limitOrderMain, priceAndAmount);
	}

	public NodeOrder bay(OrderBook orderBook, NodeUser nodeUser, BasicChangeInterface basicChange) {
		try {
			CurrencyPair currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
			List<LimitOrder> bids = orderBook.getBids();

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
			//TODO вынести в настройки трейдинг на вертуальный счет
			LimitOrderMain limitOrderMain = basicChange.placeLimitOrder(order, true);

			//<!-- Информируем о размещении ордера -->
			BigDecimal usd = CurrencyConverter.convertUsdt(amount, price);
			String message = "**[Ордер на покупку]**\n" +
					"Цена: $" + price.setScale(2, RoundingMode.HALF_UP) + "\n" +
					"Сумма ордера: $" + usd + "\n" +
					"_Статус: размещен_";
			logger.info(message + "user_id: " + nodeUser.getId() + "user_chat_id " + nodeUser.getChatId());
			producerServiceExchange.sendAnswer(message, nodeUser.getChatId());

			CountDownLatch latch = new CountDownLatch(1);
			subscribeToCurrencyRate(limitOrderMain, latch);
			latch.await();
			logger.info("ордер исполнился");


			NodeOrder nodeOrder = resultTread(nodeUser, limitOrderMain, priceAndAmount);
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
			CurrencyPair currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
			List<LimitOrder> asks = orderBook.getAsks();

			//TODO второй параметр изменить на деномический из настроек
			//<!-- назначаем минимальный спред, что бы не торговать в минус -->
			BigDecimal minPrice = FinancialCalculator.calculateMinPrice(nodeOrder.getPrice(), new BigDecimal("0.3"));

			BigDecimal price = FinancialCalculator.maxAmount(asks, minPrice);
			price = CurrencyConverter.validUsd(price);

			//<!-- конвертирую  монеты которые были купленны в валюту -->
			BigDecimal amount = CurrencyConverter.convertUsdt(nodeOrder.getOriginalAmount(), price);
			amount = CurrencyConverter.convertCurrency(price, amount);

			//<!-- Подготавливаем лимитный ордер и отправляем его на биржу -->
			List<BigDecimal> priceAndAmount = new ArrayList<>();
			priceAndAmount.add(price);
			priceAndAmount.add(amount);
			LimitOrder order = basicChange.createOrder(currencyPair, priceAndAmount, Order.OrderType.ASK);
			//TODO вынести в настройки трейдинг на вертуальный счет
			LimitOrderMain limitOrderMain = basicChange.placeLimitOrder(order, true);

			//<!-- Информируем о размещении ордера -->
			BigDecimal usd = CurrencyConverter.convertUsdt(amount, price);
			String message = "**[Ордер на продажу]**\n" +
					"Цена: $" + price.setScale(2, RoundingMode.HALF_UP) + "\n" +
					"Сумма ордера: $" + usd + "\n" +
					"_Статус: размещен_";
			logger.info(message + "user_id: " + nodeUser.getId() + "user_chat_id " + nodeUser.getChatId());
			producerServiceExchange.sendAnswer(message, nodeUser.getChatId());

			CountDownLatch latch = new CountDownLatch(1);
			subscribeToCurrencyRate(limitOrderMain, latch);
			latch.await();
			logger.info("ордер исполнился");


			NodeOrder resulOrder = resultTread(nodeUser, limitOrderMain, priceAndAmount);

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

		Date date = null;
		if (limitOrderMain.getOrderMain().getTimestamp() != null){
			date = limitOrderMain.getOrderMain().getTimestamp();
		}else {
			date = new Date();
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
		String message = "**[Выполнение ордера]**\n" +
				"Цена: $" + price + "\n" +
				"_Статус: исполнен_";
		producerServiceExchange.sendAnswer(message, nodeUser.getChatId());
		ordersDAO.save(nodeOrderResult);
		nodeUser.getOrders().add(nodeOrderResult);
		nodeUserDAO.save(nodeUser);
		return nodeOrderResult;
	}

	public void subscribeToCurrencyRate(LimitOrderMain limitOrderMain, CountDownLatch latch) {
		if (limitOrderMain.getOrderMain().getType().equals(Order.OrderType.BID)){
			subject.getCurrencyRateStream()
					.filter(rate -> rate.compareTo(limitOrderMain.getLimitPrice()) < 0)
					.firstElement()
					.subscribe(
							rate -> {
								// Уведомляем наблюдателя о достижении курса
								System.out.println("Курс достиг значения: " + rate);
								latch.countDown();
							}, error -> {
								// Обработка ошибок
								System.err.println("Произошла ошибка: " + error.getMessage());
								latch.countDown();
							});
			return;
		}
		if (limitOrderMain.getOrderMain().getType().equals(Order.OrderType.ASK)) {
			subject.getCurrencyRateStream()
					.filter(rate -> rate.compareTo(limitOrderMain.getLimitPrice()) > 0)
					.firstElement()
					.subscribe(
							rate -> {
								// Уведомляем наблюдателя о достижении курса
								System.out.println("Курс достиг значения: " + rate);
								latch.countDown();
							}, error -> {
								// Обработка ошибок
								System.err.println("Произошла ошибка: " + error.getMessage());
								latch.countDown();
							});
		}else {
			subject.getCurrencyRateStream()
					.filter(rate -> rate.compareTo(limitOrderMain.getLimitPrice()) == 0)
					.firstElement()
					.subscribe(
							rate -> {
								// Уведомляем наблюдателя о достижении курса
								System.out.println("Курс достиг значения: " + rate);
								latch.countDown();
							}, error -> {
								// Обработка ошибок
								System.err.println("Произошла ошибка: " + error.getMessage());
								latch.countDown();
							});
		}
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
