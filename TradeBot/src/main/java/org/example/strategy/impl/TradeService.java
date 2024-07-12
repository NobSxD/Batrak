package org.example.strategy.impl;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
import java.util.List;

@Component
@RequiredArgsConstructor
public class TradeService implements Observer<BigDecimal>, StrategyTrade {
	private static final Logger logger = Logger.getLogger(SlidingProtectiveOrder.class);
	private BasicChangeInterface basicChange;
	private final ProcessServiceCommand producerServiceExchange;
	private final NodeUserDAO nodeUserDAO;
	private final NodeOrdersDAO ordersDAO;
	private final StatisticsTradeDAO statisticsTradeDAO;

	@Autowired
	private WebSocketBinanceChange subject;

	private Disposable disposable;

	private LimitOrderMain limitOrderMain;
	private String message;
	List<BigDecimal> priceAndAmount;
	private NodeUser nodeUser;
	NodeOrder orderBay;

	public Observable<String> executeTrade(NodeUser nodeUser, BasicChangeInterface basicChange)  {
		this.basicChange = basicChange;
		return Observable.fromCallable(() -> {
			// Получение списка ордеров в стакане
			OrderBook orderBook = basicChange.orderBooksLimitOrders(nodeUser.getConfigTrade().getDepthGlass(), nodeUser);
			Statistics statistics = nodeUser.getStatistics();

			if (nodeUser.getStateTrade().equals(TradeState.BAY)) {
				return bay(orderBook, nodeUser, statistics);
			}

			if (nodeUser.getStateTrade().equals(TradeState.SEL)) {
				return sel(orderBook, nodeUser, orderBay, statistics);
			}

			return "Не удалось выполнить ордер";
		}).subscribeOn(Schedulers.io());
	}

	public String bay(OrderBook orderBook, NodeUser nodeUser, Statistics statistics) {
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
			limitOrderMain = basicChange.placeLimitOrder(order);

			//<!-- Информируем о размещении ордера -->
			BigDecimal usd = CurrencyConverter.convertUsdt(amount, price);
			message = "Ордер на покупку по прайсу $" + price.setScale(2, RoundingMode.HALF_UP) + " был размещен \n" +
					"сумма ордера $" + usd;

			this.nodeUser = nodeUser;
			this.priceAndAmount = priceAndAmount;
			subject.addObserver(this);
			resultTread(message, nodeUser, limitOrderMain, priceAndAmount);

			nodeUser.setStateTrade(TradeState.SEL);
		}catch (FundsExceededException e){
			producerServiceExchange.sendAnswer("Не достаточно денег на балансе", nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
		}catch (Exception e){
			producerServiceExchange.sendAnswer(e.getMessage(), nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
		}
		return "";
	}

	public String sel(OrderBook orderBook, NodeUser nodeUser, NodeOrder nodeOrder, Statistics statistics) {
		try {
			CurrencyPair currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
			List<LimitOrder> asks = orderBook.getAsks();

			//TODO второй параметр изменить на деномический из настроек
			//<!-- назначаем минимальный спред, что бы не торговать в минус -->
			BigDecimal minPrice = FinancialCalculator.calculateMinPrice(nodeOrder.getPrice(), new BigDecimal("0.3"));

			BigDecimal price = FinancialCalculator.maxAmount(asks, minPrice);
			price = CurrencyConverter.validUsd(price);

			//<!-- конвертирую  монеты которые были купленны в $ -->
			BigDecimal amount = CurrencyConverter.convertUsdt(nodeOrder.getOriginalAmount(), price);

			//<!-- Подготавливаем лимитный ордер и отправляем его на биржу -->
			List<BigDecimal> priceAndAmount = new ArrayList<>();
			priceAndAmount.add(price);
			priceAndAmount.add(amount);
			LimitOrder order = basicChange.createOrder(currencyPair, priceAndAmount, Order.OrderType.ASK);
			limitOrderMain = basicChange.placeLimitOrder(order);


			//<!-- Информируем о размещении ордера -->
			BigDecimal usd = CurrencyConverter.convertUsdt(amount, price);
			message = "Ордер на продажу по прайсу $" + price.setScale(2, RoundingMode.HALF_UP) + " был размещен \n" +
					"сумма ордера $" + usd;

			this.nodeUser = nodeUser;
			this.priceAndAmount = priceAndAmount;
			subject.addObserver(this);
			resultTread(message, nodeUser, limitOrderMain, priceAndAmount);

			nodeUser.setStateTrade(TradeState.BAY);
		}catch (FundsExceededException e){
			producerServiceExchange.sendAnswer("Не достаточно денег на балансе", nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
		}
		catch (Exception e){
			producerServiceExchange.sendAnswer(e.getMessage(), nodeUser.getChatId());
			logger.error(e.getMessage());
			nodeUser.setTradeStartOrStop(false);
		}
		return "";
	}

	public void resultTread(String message, NodeUser nodeUser, LimitOrderMain limitOrderMain, List<BigDecimal> priceAndAmount){
		BigDecimal price = priceAndAmount.get(0);
		BigDecimal amount = priceAndAmount.get(1);
		logger.info(message + "user_id: " + nodeUser.getId() + "user_chat_id " + nodeUser.getChatId());
		producerServiceExchange.sendAnswer(message, nodeUser.getChatId());

		message = "Ордер на покупку по прайсу $" + price.setScale(2, RoundingMode.HALF_UP) + " был выполнен \n" +
				"сумма ордера $" + amount;
		producerServiceExchange.sendAnswer(message, nodeUser.getChatId());
		logger.info(limitOrderMain.getOrderMain().getId() + " : ордер был исполнен по прайсу " + limitOrderMain.getLimitPrice());

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
		this.nodeUser = nodeUser;
		subject.removeObserver(disposable);
		producerServiceExchange.sendAnswer("Ордер по курсу $" + price + " был исполнен", nodeUser.getChatId());
		ordersDAO.save(nodeOrderResult);
		nodeUserDAO.save(nodeUser);
	}

	@Override
	public void onSubscribe(@NonNull Disposable d) {
		this.disposable = d;
	}

	@Override
	public void onNext(@NonNull BigDecimal bigDecimal) {
		if (bigDecimal.compareTo(limitOrderMain.getLimitPrice()) == 0){
			resultTread(message, nodeUser , limitOrderMain, priceAndAmount);
		}
	}

	@Override
	public void onError(@NonNull Throwable e) {

	}

	@Override
	public void onComplete() {

	}

	@Override
	public String trade(NodeUser nodeUser, BasicChangeInterface basicChange) {
		 executeTrade(nodeUser, basicChange);
		return "";
	}

	@Override
	public StrategyEnams getType() {
		return StrategyEnams.SlidingProtectiveOrder;
	}
}
