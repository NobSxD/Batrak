package org.example.strategy.impl;

import org.example.finance.FinancialCalculator;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.StrategyTrade;
import org.example.finance.CurrencyConverter;
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
import org.example.xchange.BasicChangeInterface;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.FundsExceededException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SlidingProtectiveOrder implements StrategyTrade {
	private BasicChangeInterface basicChange;
	private final ProcessServiceCommand producerServiceExchange;
	private final NodeUserDAO nodeUserDAO;
	private final NodeOrdersDAO ordersDAO;
	private final StatisticsTradeDAO statisticsTradeDAO;
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

	@Override
	public StrategyEnams getType() {
		return StrategyEnams.SlidingProtectiveOrder;
	}

	public NodeOrder bay(OrderBook orderBook, NodeUser nodeUser) {
		try {
			CurrencyPair currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
			List<LimitOrder> bids = orderBook.getBids();

			BigDecimal wallBayOrder = FinancialCalculator.maxAmount(bids);
			wallBayOrder = CurrencyConverter.validUsd(wallBayOrder);

			BigDecimal summa = nodeUser.getConfigTrade().getAmountOrder();
			summa = CurrencyConverter.convertCurrency(wallBayOrder, summa);


			String orderIdBids = basicChange.limitOrder(Order.OrderType.BID, summa, wallBayOrder, currencyPair);

			BigDecimal usd = CurrencyConverter.convertUsdt(summa, wallBayOrder);
			String message = "Ордер на покупку по прайсу $" + wallBayOrder.setScale(2, RoundingMode.HALF_UP) + " был размещен \n" +
			"сумма ордера $" + usd;
			logger.info(message + "user_id: " + nodeUser.getId() + "user_chat_id " + nodeUser.getChatId());
			producerServiceExchange.sendAnswer(message, nodeUser.getChatId());

			while (! basicChange.isOrderExecuted(orderIdBids)) {
				if (!nodeUser.isTradeStartOrStop()){
					producerServiceExchange.sendAnswer("Трейдин досрочно завершен", nodeUser.getChatId());
					break;
				}
				Thread.sleep(5000);
			}
			final NodeOrder nodeOrder = NodeOrder.builder()
					.price(wallBayOrder)
					.summaCurrency(summa)
					.summaUSD(usd)
					.orderType(OrderType.BIDS)
					.pair(nodeUser.getConfigTrade().getNamePair())
					.createDate(new Date())
					.strategyEnams(nodeUser.getConfigTrade().getStrategy())
					.nodeUser(nodeUser)
					.build();
			producerServiceExchange.sendAnswer("Ордер по курсу $" + wallBayOrder + " был исполнен", nodeUser.getChatId());
			nodeUser.setStateTrade(TradeState.SEL);
			nodeUserDAO.save(nodeUser);
			return nodeOrder;
		}catch (FundsExceededException e){
			producerServiceExchange.sendAnswer("Не достаточно денег на балансе", nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			nodeUserDAO.save(nodeUser);
			return null;
		}catch (Exception e){
			producerServiceExchange.sendAnswer(e.getMessage(), nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			nodeUserDAO.save(nodeUser);
			return null;
		}
	}

	public NodeOrder sel(OrderBook orderBook, NodeUser nodeUser, NodeOrder nodeOrder){
		try {
			CurrencyPair currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
			List<LimitOrder> asks = orderBook.getAsks();

			//TODO второй параметр изменить на деномический из настроек
			BigDecimal minPrice = FinancialCalculator.calculateMinPrice(nodeOrder.getPrice(), new BigDecimal("0.5"));

			BigDecimal wallSellOrder = FinancialCalculator.maxAmount(asks, minPrice);
			wallSellOrder = CurrencyConverter.validUsd(wallSellOrder);


			String orderIdAsks = basicChange.limitOrder(Order.OrderType.ASK, nodeOrder.getSummaCurrency(), wallSellOrder, currencyPair);

			BigDecimal usd = CurrencyConverter.convertUsdt(nodeOrder.getSummaCurrency(), wallSellOrder);

			producerServiceExchange.sendAnswer("Ордер на продажу по прайсу $" + wallSellOrder + " был размещен \n" +
													   "сумма ордера $" + usd, nodeUser.getChatId());

			while (! basicChange.isOrderExecuted(orderIdAsks)) {
				if (!nodeUser.isTradeStartOrStop()){
					producerServiceExchange.sendAnswer("Трейдин досрочно завершен", nodeUser.getChatId());
					break;
				}
				Thread.sleep(5000);
			}
			final NodeOrder order = NodeOrder.builder()
					.price(wallSellOrder)
					.summaCurrency(nodeOrder.getSummaCurrency())
					.summaUSD(usd)
					.orderType(OrderType.ASKS)
					.pair(nodeUser.getConfigTrade().getNamePair())
					.createDate(new Date())
					.nodeUser(nodeUser)
					.strategyEnams(nodeUser.getConfigTrade().getStrategy())
					.build();
			producerServiceExchange.sendAnswer("Ордер по курсу $" + wallSellOrder + " был исполнен", nodeUser.getChatId());
			nodeUser.setStateTrade(TradeState.BAY);
			nodeUserDAO.save(nodeUser);
			return order;
		}catch (FundsExceededException e){
			producerServiceExchange.sendAnswer("Не достаточно денег на балансе", nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			nodeUserDAO.save(nodeUser);
			return null;
		}
		catch (Exception e){
			producerServiceExchange.sendAnswer(e.getMessage(), nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
			nodeUserDAO.save(nodeUser);
			return null;
		}

	}





}
