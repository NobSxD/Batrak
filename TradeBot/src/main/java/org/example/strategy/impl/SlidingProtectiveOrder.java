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
import org.example.observer.CurrentConditionsDisplay;
import org.example.observer.pair.DataBtcUsdtImpl;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.StrategyTrade;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.DTO.LimitOrderMain;
import org.example.xchange.finance.CurrencyConverter;
import org.example.xchange.finance.FinancialCalculator;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.FundsExceededException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SlidingProtectiveOrder implements StrategyTrade{
	private BasicChangeInterface basicChange;
	private final ProcessServiceCommand producerServiceExchange;
	private final NodeUserDAO nodeUserDAO;
	private final NodeOrdersDAO ordersDAO;
	private final StatisticsTradeDAO statisticsTradeDAO;
	private CurrentConditionsDisplay currentConditionsDisplay;
	private static final Logger logger = Logger.getLogger(SlidingProtectiveOrder.class);

	public  String trade(NodeUser nodeUser, BasicChangeInterface basicChange){ // необходимые параметры, сумма трейда, валютная пара, глудина стакана,
		this.basicChange=basicChange;

		NodeOrder orderSel = null;
		NodeOrder orderBay = null;


		//1. Получить список ордеров в стакане
		try {
			OrderBook orderBook = basicChange.orderBooksLimitOrders(nodeUser.getConfigTrade().getDepthGlass(), nodeUser);
			Statistics statistics = nodeUser.getStatistics();

			if (nodeUser.getStateTrade().equals(TradeState.BAY)) {
				//TODO сделать сохранение ордера в бд
				orderBay = bay(orderBook, nodeUser);
				ordersDAO.save(orderBay);
				statistics.setCountDealBay(statistics.getCountDealBay() +1);
			}
			if (nodeUser.getStateTrade().equals(TradeState.SEL) && orderBay != null) {
				orderSel = sel(orderBook, nodeUser, orderBay);
				ordersDAO.save(orderSel);
				statistics.setCountDealSel(statistics.getCountDealSel() + 1);

				BigDecimal profitPercentage = FinancialCalculator.calculateProfitPercentage(nodeUser.getConfigTrade().getAmountOrder(), orderBay.getPrice(), orderSel.getPrice());
				BigDecimal dollarAmount = FinancialCalculator.calculateDollarAmount(nodeUser.getConfigTrade().getAmountOrder(), orderBay.getPrice(), orderSel.getPrice());

				statistics.setCountDeal(statistics.getCountDeal() + 1);
				statistics.setProfit(statistics.getProfit().add(dollarAmount).setScale(2, RoundingMode.HALF_UP));
				statisticsTradeDAO.save(statistics);
				nodeUserDAO.save(nodeUser);

				return "Цена открытие ордера= " + orderBay.getPrice() + "$\n"
						+ "Цена закритие ордера= " + orderSel.getPrice() + "$\n"
						+ "Профит в процентах= " + profitPercentage + "%\n"
						+ "Профит в  доллорах = $" + dollarAmount;

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			return "Во время выполнения троговли произошла ошибка ";
		}
		return "Не удалось выполнить ордер";
	}


	public NodeOrder bay(OrderBook orderBook, NodeUser nodeUser) {
		try {
			CurrencyPair currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
			List<LimitOrder> bids = orderBook.getBids();

			BigDecimal price = FinancialCalculator.maxAmount(bids);
			price = CurrencyConverter.validUsd(price);
			//<!-- конвертирую  установленную сумму $ -->
			BigDecimal amount = nodeUser.getConfigTrade().getAmountOrder();
			amount = CurrencyConverter.convertCurrency(price, amount);

			//<!-- Подготавливаем лимитный ордер и отправляем его на биржу -->
			List<BigDecimal> priceAndAmount = new ArrayList<>();
			priceAndAmount.add(price);
			priceAndAmount.add(amount);
			LimitOrder order = basicChange.createOrder(currencyPair, priceAndAmount, Order.OrderType.BID);
			LimitOrderMain limitOrderMain = basicChange.placeLimitOrder(order);

			//<!-- Информируем о размещении ордера -->
			BigDecimal usd = CurrencyConverter.convertUsdt(amount, price);
			String message = "Ордер на покупку по прайсу $" + price.setScale(2, RoundingMode.HALF_UP) + " был размещен \n" +
			"сумма ордера $" + usd;
			NodeOrder resultOrder = resultTread(message, nodeUser, limitOrderMain, priceAndAmount);

			nodeUser.setStateTrade(TradeState.SEL);
			return resultOrder;
		}catch (FundsExceededException e){
			producerServiceExchange.sendAnswer("Не достаточно денег на балансе", nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			return null;
		}catch (Exception e){
			producerServiceExchange.sendAnswer(e.getMessage(), nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			return null;
		}
	}

	public NodeOrder sel(OrderBook orderBook, NodeUser nodeUser, NodeOrder nodeOrder){
		try {
			CurrencyPair currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
			List<LimitOrder> asks = orderBook.getAsks();

			//TODO второй параметр изменить на деномический из настроек
			//<!-- назначаем минимальный спред, что бы не торговать в минус -->
			BigDecimal minPrice = FinancialCalculator.calculateMinPrice(nodeOrder.getPrice(), new BigDecimal("0.5"));

			BigDecimal price = FinancialCalculator.maxAmount(asks, minPrice);
			price = CurrencyConverter.validUsd(price);

			//<!-- конвертирую  монеты которые были купленны в $ -->
			BigDecimal amount = CurrencyConverter.convertUsdt(nodeOrder.getOriginalAmount(), price);

			//<!-- Подготавливаем лимитный ордер и отправляем его на биржу -->
			List<BigDecimal> priceAndAmount = new ArrayList<>();
			priceAndAmount.add(price);
			priceAndAmount.add(amount);
			LimitOrder order = basicChange.createOrder(currencyPair, priceAndAmount, Order.OrderType.ASK);
			LimitOrderMain limitOrderMain = basicChange.placeLimitOrder(order);

			//<!-- Информируем о размещении ордера -->
			BigDecimal usd = CurrencyConverter.convertUsdt(amount, price);
			String message = "Ордер на продажу по прайсу $" + price.setScale(2, RoundingMode.HALF_UP) + " был размещен \n" +
					"сумма ордера $" + usd;

			NodeOrder resultOrder = resultTread(message, nodeUser, limitOrderMain, priceAndAmount);

			nodeUser.setStateTrade(TradeState.BAY);
			return resultOrder;
		}catch (FundsExceededException e){
			producerServiceExchange.sendAnswer("Не достаточно денег на балансе", nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			return null;
		}
		catch (Exception e){
			producerServiceExchange.sendAnswer(e.getMessage(), nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			return null;
		}

	}

	public NodeOrder resultTread(String message, NodeUser nodeUser, LimitOrderMain limitOrderMain, List<BigDecimal> priceAndAmount){
		BigDecimal price = priceAndAmount.get(0);
		BigDecimal amount = priceAndAmount.get(1);
		logger.info(message + "user_id: " + nodeUser.getId() + "user_chat_id " + nodeUser.getChatId());
		producerServiceExchange.sendAnswer(message, nodeUser.getChatId());

		currentConditionsDisplay = new CurrentConditionsDisplay(new DataBtcUsdtImpl());
		BigDecimal display = currentConditionsDisplay.display();
		if (display.compareTo(price) > 0){
			message = "Ордер на покупку по прайсу $" + price.setScale(2, RoundingMode.HALF_UP) + " был выполнен \n" +
					"сумма ордера $" + amount;
			producerServiceExchange.sendAnswer(message, nodeUser.getChatId());
			logger.info(limitOrderMain.getOrderMain().getId() + " : ордер был исполнен по прайсу " + limitOrderMain.getLimitPrice());
			currentConditionsDisplay.undSubscribe();
		}
		final NodeOrder nodeOrderResult = NodeOrder.builder()
				.price(price)
				.orderId(limitOrderMain.getOrderMain().getId())
				.originalAmount(limitOrderMain.getOrderMain().getOriginalAmount())
				.summaUSD(amount)
				.orderType(OrderType.BIDS)
				.pair(nodeUser.getConfigTrade().getNamePair())
				.createDate(limitOrderMain.getOrderMain().getTimestamp())
				.strategyEnams(nodeUser.getConfigTrade().getStrategy())
				.nodeUser(nodeUser)
				.build();
		producerServiceExchange.sendAnswer("Ордер по курсу $" + price + " был исполнен", nodeUser.getChatId());
		return nodeOrderResult;
	}



	@Override
	public StrategyEnams getType() {
		return StrategyEnams.SlidingProtectiveOrder;
	}



}
