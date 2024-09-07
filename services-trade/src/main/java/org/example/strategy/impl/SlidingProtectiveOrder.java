package org.example.strategy.impl;

import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.ConfigTrade;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.menu.MenuStrategy;
import org.example.entity.enams.state.OrderState;
import org.example.entity.enams.state.TradeState;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.StrategyTrade;
import org.example.strategy.impl.helper.AssistantMessage;
import org.example.strategy.impl.helper.AssistantObserver;
import org.example.websocet.WebSocketChange;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.finance.CurrencyConverter;
import org.example.xchange.finance.FinancialCalculator;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.FundsExceededException;
import org.knowm.xchange.instrument.Instrument;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.example.strategy.impl.helper.CreateNodeOrder.createNodeOrder;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlidingProtectiveOrder implements StrategyTrade {

    private final ProcessServiceCommand producerServiceExchange;
    private final WebSocketCommand webSocketCommand;
    private final NodeUserDAO nodeUserDAO;
    private final NodeOrdersDAO ordersDAO;

    @Override
    @Transactional
    public String trade(NodeUser nodeUser, BasicChangeInterface basicChange) {
        NodeOrder order = null;
        while (nodeUser.isTradeStartOrStop()) {
            // Получаем обновленное состояние пользователя для проверки
            Optional<NodeUser> byId = nodeUserDAO.findById(nodeUser.getId());
            nodeUser = byId.orElse(nodeUser);

            // Получение списка ордеров в стакане
            OrderBook orderBook = basicChange.orderBooksLimitOrders(
                    nodeUser.getConfigTrade().getDepthGlass(),
                    nodeUser.getConfigTrade().getNamePair()
            );


            switch (nodeUser.getStateTrade()) {
                case BAY -> {
                    if (order == null) {
                        order = bay(orderBook, nodeUser, basicChange);
                    }
                }
                case SEL -> {
                    if (order != null && !order.getOrderState().equals(OrderState.CANCELLED)) {
                        sel(orderBook, nodeUser, order, basicChange);
                    }
                }
                default -> {
                    log.error("Состояние трейлинга, равна= {}. id= {}", nodeUser.getStateTrade(), nodeUser.getId());
                }
            }
        }
        List<NodeOrder> nodeOrders = ordersDAO.findAllOrdersFromTimestampAndNodeUser(
                nodeUser.getLastStartTread(), nodeUser);
        producerServiceExchange.sendAnswer(
                AssistantMessage.messageResult(
                        AssistantObserver.calculateProfit(nodeOrders),
                        nodeOrders.size()), nodeUser.getChatId());
        return "";
    }

    public NodeOrder bay(OrderBook orderBook, NodeUser nodeUser, BasicChangeInterface basicChange) {
        List<LimitOrder> bids = orderBook.getBids();
        ConfigTrade configTrade = nodeUser.getConfigTrade();
        Instrument currencyPair = new CurrencyPair(configTrade.getNamePair());
        BigDecimal price = FinancialCalculator.maxAmount(bids);
        BigDecimal amount = CurrencyConverter.convertCurrency(price, nodeUser.getConfigTrade().getAmountOrder(), configTrade.getScale());
        List<BigDecimal> priceAndAmount = List.of(price, amount);

        try {
            return processOrder(nodeUser, basicChange, currencyPair, priceAndAmount, Order.OrderType.BID, true);
        } catch (FundsExceededException e) {
            return handleFundsExceededException(nodeUser, e);
        } catch (Exception e) {
            return handleGeneralException(nodeUser, e);
        }
    }

    public NodeOrder sel(OrderBook orderBook, NodeUser nodeUser, NodeOrder nodeOrder, BasicChangeInterface basicChange) {
        List<LimitOrder> asks = orderBook.getAsks();
        Instrument currencyPair = new CurrencyPair(nodeOrder.getInstrument());
        BigDecimal price = CurrencyConverter.validUsd(FinancialCalculator.maxAmount(asks));
        BigDecimal amount = nodeOrder.getOriginalAmount();
        List<BigDecimal> priceAndAmount = List.of(price, amount);

        try {
            return processOrder(nodeUser, basicChange, currencyPair, priceAndAmount, Order.OrderType.ASK, false);
        } catch (FundsExceededException e) {
            return handleFundsExceededException(nodeUser, e);
        } catch (Exception e) {
            return handleGeneralException(nodeUser, e);
        }
    }

    private NodeOrder processOrder(NodeUser nodeUser, BasicChangeInterface basicChange,
                                   Instrument currencyPair, List<BigDecimal> priceAndAmount, Order.OrderType orderType,
                                   boolean isBid) throws InterruptedException {
        CurrencyPair instrument = new CurrencyPair("BTC-USDT");
        BigDecimal price = CurrencyConverter.convertCurrency(new BigDecimal("53231.39000000"), new BigDecimal("11.2"), 5);
        List<BigDecimal> priceAndAmount2 = List.of(new BigDecimal("50000.39000000"),price);
        LimitOrder order = basicChange.createOrder(instrument, priceAndAmount2, Order.OrderType.BID);
        String orderId = basicChange.placeLimitOrder(order, nodeUser.getConfigTrade().isRealTrade());
        NodeOrder nodeOrder = createNodeOrder(order, orderId, priceAndAmount, nodeUser, OrderState.PLACED);
        nodeUser.getOrders().add(nodeOrder);

        // Информируем о размещении ордера
        String message = isBid ? AssistantMessage.messageBuy(nodeOrder.getUsd(), nodeOrder.getLimitPrice())
                : AssistantMessage.messageSell(nodeOrder.getUsd(), nodeOrder.getLimitPrice());
        producerServiceExchange.sendAnswer(message, nodeOrder.getNodeUser().getChatId());

        initiateWebSocketSubscription(nodeOrder, nodeUser);

        if (!awaitTradeCompletion(nodeOrder, nodeUser, basicChange)) {
            return nodeOrder;
        }
        finalizeOrder(nodeOrder, nodeUser);
        nodeUser.setStateTrade(isBid ? TradeState.SEL : TradeState.BAY);
        return nodeOrder;
    }

    private void initiateWebSocketSubscription(NodeOrder nodeOrder, NodeUser nodeUser) {
        CountDownLatch latch = new CountDownLatch(1);
        WebSocketChange webSocketChange = webSocketCommand.webSocketChange(nodeUser);
        Observable<NodeOrder> cancelTrade = PublishSubject.create();
        AssistantObserver.subscribeToCurrencyRate(nodeOrder, latch, webSocketChange, cancelTrade);
    }

    private boolean awaitTradeCompletion(NodeOrder nodeOrder, NodeUser nodeUser, BasicChangeInterface basicChange)
            throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            latch.await();
        } finally {
            if (nodeOrder.getOrderState().equals(OrderState.PENDING_CANCEL)) {
                basicChange.cancelOrder(nodeOrder.getInstrument(), nodeOrder.getOrderId());
                producerServiceExchange.sendAnswer(AssistantMessage.messageCancel(nodeOrder.getOrderId()), nodeUser.getChatId());
                nodeUser.setTradeStartOrStop(false);
                nodeOrder.setOrderState(OrderState.CANCELLED);
                ordersDAO.save(nodeOrder);
                nodeUserDAO.save(nodeUser);
                return false;
            }
        }
        return true;
    }

    private void finalizeOrder(NodeOrder nodeOrder, NodeUser nodeUser) {
        log.info("{} : ордер был исполнен по прайсу {}. ", nodeOrder.getOrderId(), nodeOrder.getLimitPrice());
        String message = AssistantMessage.messageProcessing(nodeOrder.getLimitPrice());
        producerServiceExchange.sendAnswer(message, nodeOrder.getNodeUser().getChatId());
        nodeOrder.setOrderState(OrderState.FILLED);
        ordersDAO.save(nodeOrder);
        nodeUserDAO.save(nodeUser);
    }

    private NodeOrder handleFundsExceededException(NodeUser nodeUser, FundsExceededException e) {
        producerServiceExchange.sendAnswer("Не достаточно денег на балансе: " + e.getMessage(), nodeUser.getChatId());
        log.error("Insufficient funds error: {}", e.getMessage());
        nodeUser.setTradeStartOrStop(false);
        throw new RuntimeException(e);
    }

    private NodeOrder handleGeneralException(NodeUser nodeUser, Exception e) {
        producerServiceExchange.sendAnswer("Ошибка: " + e.getMessage(), nodeUser.getChatId());
        log.error("General error: {}", e.getMessage());
        nodeUser.setTradeStartOrStop(false);
        throw new RuntimeException(e);
    }

    @Override
    public MenuStrategy getType() {
        return MenuStrategy.SlidingProtectiveOrder;
    }
}
