package org.example.service.impl.strategy;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.command.finance.ProfitCalculator;
import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.dao.StatisticsTradeDAO;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.Statistics;
import org.example.entity.enams.OrderType;
import org.example.entity.enams.TradeState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.example.service.Strategy;
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
public class SlidingProtectiveOrder implements Strategy {
	private BasicChangeInterface basicChange;
	private final ProcessServiceCommand producerServiceExchange;
	private final NodeUserDAO nodeUserDAO;
	private final StatisticsTradeDAO statisticsTradeDAO;
	private final NodeOrdersDAO ordersDAO;
	private static final Logger logger = Logger.getLogger(SlidingProtectiveOrder.class);

	public  String slidingProtectiveOrder(NodeUser nodeUser, BasicChangeInterface basicChange){ // необходимые параметры, сумма трейда, валютная пара, глудина стакана,
		this.basicChange=basicChange;

		NodeOrder orderSel = null;
		NodeOrder orderBay = null;


		//1. Получить список ордеров в стакане
		try {
			OrderBook orderBook = basicChange.orderBooksLimitOrders(nodeUser.getSettingsTrade().getDepthGlass(), nodeUser);
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

				BigDecimal profitPercentage = ProfitCalculator.calculateProfitPercentage(nodeUser.getSettingsTrade().getAmountOrder(), orderBay.getPrice(), orderSel.getPrice());
				BigDecimal dollarAmount = ProfitCalculator.calculateDollarAmount(nodeUser.getSettingsTrade().getAmountOrder(), orderBay.getPrice(), orderSel.getPrice());

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
			CurrencyPair currencyPair = new CurrencyPair(nodeUser.getSettingsTrade().getNamePair());
			List<LimitOrder> bids = orderBook.getBids();

			BigDecimal wallBayOrder = ProfitCalculator.maxAmount(bids);
			wallBayOrder = ProfitCalculator.validUsd(wallBayOrder);

			BigDecimal summa = nodeUser.getSettingsTrade().getAmountOrder();
			summa = ProfitCalculator.convertCurrency(wallBayOrder, summa);


			String orderIdBids = basicChange.limitOrder(Order.OrderType.BID, summa, wallBayOrder, currencyPair);

			BigDecimal usd = ProfitCalculator.convertUsdt(summa, wallBayOrder);
			producerServiceExchange.sendAnswer("Ордер на покупку по прайсу $" + wallBayOrder.setScale(2, RoundingMode.HALF_UP) + " был размещен \n" +
													   "сумма ордера $" + usd, nodeUser.getChatId());

			while (! basicChange.isOrderExecuted(orderIdBids)) {
				Thread.sleep(5000);
			}
			NodeOrder nodeOrder = NodeOrder.builder()
					.price(wallBayOrder)
					.summaCurrency(summa)
					.summaUSD(usd)
					.orderType(OrderType.BIDS)
					.pair(nodeUser.getSettingsTrade().getNamePair())
					.createDate(new Date())
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
			CurrencyPair currencyPair = new CurrencyPair(nodeUser.getSettingsTrade().getNamePair());
			List<LimitOrder> asks = orderBook.getAsks();

			//TODO второй параметр изменить на деномический из настроек
			BigDecimal minPrice = ProfitCalculator.calculateMinPrice(nodeOrder.getPrice(), new BigDecimal("0.5"));

			BigDecimal wallSellOrder = ProfitCalculator.maxAmount(asks, minPrice);
			wallSellOrder = ProfitCalculator.validUsd(wallSellOrder);


			String orderIdAsks = basicChange.limitOrder(Order.OrderType.ASK, nodeOrder.getSummaCurrency(), wallSellOrder, currencyPair);

			BigDecimal usd = ProfitCalculator.convertUsdt(nodeOrder.getSummaCurrency(), wallSellOrder);

			producerServiceExchange.sendAnswer("Ордер на продажу по прайсу $" + wallSellOrder + " был размещен \n" +
													   "сумма ордера $" + usd, nodeUser.getChatId());

			while (! basicChange.isOrderExecuted(orderIdAsks)) {
				Thread.sleep(5000);
			}
			NodeOrder order = NodeOrder.builder()
					.price(wallSellOrder)
					.summaCurrency(nodeOrder.getSummaCurrency())
					.summaUSD(usd)
					.orderType(OrderType.ASKS)
					.pair(nodeUser.getSettingsTrade().getNamePair())
					.createDate(new Date())
					.nodeUser(nodeUser)
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
