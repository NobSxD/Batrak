package org.example.strategy.impl;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.OrderState;
import org.example.entity.enams.state.TradeState;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.Strategy;
import org.example.strategy.impl.helper.AssistantMessage;
import org.example.strategy.impl.helper.AssistantObserver;
import org.example.strategy.impl.helper.TradeStatusManager;
import org.example.websocet.WebSocketChange;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.FundsExceededException;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.example.strategy.impl.helper.CreateNodeOrder.createNodeOrder;

@Slf4j
public abstract class StrategyBasic implements Strategy {
    protected CountDownLatch latch = new CountDownLatch(1);
    protected BasicChangeInterface basicChange;

    /**
     * RabbitMq для отпраки пользователских сообщений
     */
    /**
     * RabbitMq для отпраки пользователских сообщений
     */
    protected ProcessServiceCommand producerServiceExchange;
    /**
     * Сокет для получение данных с биржи
     */
    protected WebSocketCommand webSocketCommand;

    /**
     * Получить пользователя из Cache
     */
    protected Cache<Long, NodeUser> nodeUserCache;

    /**
     * Интерфейс для работы с биржей
     */
    protected NodeOrder order;
    protected final TradeStatusManager tradeStatusManager;

    protected StrategyBasic(TradeStatusManager tradeStatusManager) {
        this.tradeStatusManager = tradeStatusManager;
    }

    /**
     * Выполняет процесс торговли для данного пользователя. Этот метод настраивает торговую среду и
     * постоянно обновляет состояние торговли пользователя, взаимодействуя с различными компонентами,
     * такими как базы данных и команды веб-сокетов.
     *
     * <p>Этот метод требует, чтобы несколько зависимостей не были null. Если любая из этих зависимостей
     * равна null, будет выброшено {@link IllegalArgumentException}. Метод использует кэш для данных
     * пользователей для повышения эффективности и взаимодействует с различными DAO для управления
     * данными пользователей и заказами. Состояния торговли управляются и обновляются в рамках цикла,
     * чтобы обеспечить непрерывную торговлю до выполнения определенных условий.</p>
     *
     * <p>Обрабатываемые состояния торговли включают {@link TradeState#TRADE_START}, {@link TradeState#BUY}
     * и {@link TradeState#SELL}. Корректировки ордеров выполняются в зависимости от текущего состояния
     * и состояния книги ордеров. После завершения торговли или достижения состояния ошибки, сводка
     * результатов торговли отправляется пользователю через предоставленный обменный сервис.</p>
     *
     * @param nodeUser пользователь, для которого должна быть выполнена торговля; не должен быть null
     * @param producerServiceExchange сервис, ответственный за обработку команд сервиса, связанных с обменами;
     *                                не должен быть null
     * @param webSocketCommand интерфейс команды веб-сокета для обработки команд, связанных с веб-сокетами;
     *                         не должен быть null
     * @param nodeUserDAO интерфейс DAO для доступа к данным пользователей; не должен быть null
     * @param ordersDAO интерфейс DAO для доступа к данным заказов; не должен быть null
     * @param nodeUserCache кэш для доступа и хранения данных пользователей; не должен быть null
     * @param basicChange интерфейс для обработки основных изменений в торговле; не должен быть null
     * @throws IllegalArgumentException если любая из зависимостей сервиса или DAO равна null
     */
    @Transactional
    @Override
    public void trade(NodeUser nodeUser, ProcessServiceCommand producerServiceExchange, WebSocketCommand webSocketCommand,
                      NodeUserDAO nodeUserDAO, NodeOrdersDAO ordersDAO, Cache<Long, NodeUser> nodeUserCache, BasicChangeInterface basicChange) {

        if (producerServiceExchange == null) {
            throw new IllegalArgumentException("producerServiceExchange cannot be null");
        }
        if (webSocketCommand == null) {
            throw new IllegalArgumentException("webSocketCommand cannot be null");
        }
        if (nodeUserDAO == null) {
            throw new IllegalArgumentException("nodeUserDAO cannot be null");
        }
        if (ordersDAO == null) {
            throw new IllegalArgumentException("ordersDAO cannot be null");
        }
        if (nodeUserCache == null) {
            throw new IllegalArgumentException("nodeUserCache cannot be null");
        }
        if (basicChange == null) {
            throw new IllegalArgumentException("basicChange cannot be null");
        }
        this.producerServiceExchange = producerServiceExchange;
        this.webSocketCommand = webSocketCommand;
        this.nodeUserCache = nodeUserCache;
        this.basicChange = basicChange;


        while (tradeStatusManager.getCurrentTradeState().equals(TradeState.TRADE_START)
                || tradeStatusManager.getCurrentTradeState().equals(TradeState.BUY)
                || tradeStatusManager.getCurrentTradeState().equals(TradeState.SELL)) {
            /* если мы только начинаем торговать, то проверяем статус и присваиваем покупку*/
            if (tradeStatusManager.getCurrentTradeState().equals(TradeState.TRADE_START)) {
                tradeStatusManager.buy();
            }

            // Получаем обновленное состояние пользователя для проверки
            Optional<NodeUser> byId = nodeUserDAO.findById(nodeUser.getId());
            nodeUser = byId.orElse(nodeUser);


            if (tradeStatusManager.getCurrentTradeState().equals(TradeState.BUY)) {
                order = bay(nodeUser);
                //TODO поменять статус ордера на завершенный
                ordersDAO.save(order);
                nodeUserDAO.save(nodeUser);
            }
            if (tradeStatusManager.getCurrentTradeState().equals(TradeState.SELL)) {
                if (order == null){
                    producerServiceExchange.sendAnswer("Ордер на покупку не был найден, остановите торговлю " +
                            "и попробуйте запустить ее заново", nodeUser.getChatId());
                    throw new  IllegalArgumentException("order cannot be null");
                }
                order = sel(nodeUser);
                //TODO поменять статус ордера на завершенный
                ordersDAO.save(order);
                nodeUserDAO.save(nodeUser);
            }

        }
        List<NodeOrder> nodeOrders = ordersDAO.findAllOrdersFromTimestampAndNodeUser( //TODO выбрать только исполненые ордера, учитовать минуты при старте
                nodeUser.getLastStartTread(), nodeUser);
        producerServiceExchange.sendAnswer(
                AssistantMessage.messageResult(
                        AssistantObserver.calculateProfit(nodeOrders),
                        nodeOrders.size()), nodeUser.getChatId());
    }

    public abstract NodeOrder bay(NodeUser nodeUser);

    public abstract NodeOrder sel(NodeUser nodeUser);

    public void cancelOrder(NodeOrder nodeOrder) {
        if (basicChange == null) {
            latch.countDown();
            throw new RuntimeException("basicChange == null");
        }
        try {
            basicChange.cancelOrder(nodeOrder.getInstrument(), nodeOrder.getOrderId());
        } catch (ExchangeException e) {
            log.error("ошибка биржи: {}", e.getMessage());
        } catch (Exception e) {
            log.error("ошибка: {}", e.getMessage());
        } finally {
            latch.countDown();
        }
    }

    @Transactional
    public NodeOrder processOrder(NodeUser nodeUser,Instrument currencyPair, List<BigDecimal> priceAndAmount,
                                  Order.OrderType orderType, boolean isBid) throws InterruptedException {
        LimitOrder order = basicChange.createOrder(currencyPair, priceAndAmount, orderType);
        String orderId = basicChange.placeLimitOrder(order, nodeUser.getConfigTrade().isEnableDemoTrading());
        NodeOrder nodeOrder = createNodeOrder(order, orderId, priceAndAmount, nodeUser, OrderState.PLACED);
        nodeUser.getOrders().add(nodeOrder);
        nodeUserCache.put(nodeUser.getId(), nodeUser);

        // Информируем о размещении ордера
        String message = isBid ? AssistantMessage.messageBuy(nodeOrder.getUsd(), nodeOrder.getLimitPrice())
                : AssistantMessage.messageSell(nodeOrder.getUsd(), nodeOrder.getLimitPrice());
        producerServiceExchange.sendAnswer(message, nodeOrder.getNodeUser().getChatId());

        initiateWebSocketSubscription(nodeOrder, nodeUser, orderType);

        if (!awaitTradeCompletion(nodeOrder, nodeUser)) {
            return nodeOrder;
        }
        finalizeOrder(nodeOrder);

        return nodeOrder;
    }

    private void initiateWebSocketSubscription(NodeOrder nodeOrder, NodeUser nodeUser, Order.OrderType orderType) {
        WebSocketChange webSocketChange = webSocketCommand.webSocketChange(nodeUser);
        AssistantObserver.subscribeToCurrencyRate(nodeOrder, latch, webSocketChange, orderType, tradeStatusManager);
    }

    private boolean awaitTradeCompletion(NodeOrder nodeOrder, NodeUser nodeUser)
            throws InterruptedException {
        try {
            latch.await();
        } finally {
            if (nodeOrder.getOrderState().equals(OrderState.PENDING_CANCEL)) {
                producerServiceExchange.sendAnswer(AssistantMessage.messageCancel(nodeOrder.getOrderId()), nodeUser.getChatId());
                tradeStatusManager.cancelTrading();
                nodeOrder.setOrderState(OrderState.CANCELLED);
                return false;
            }
        }
        return true;
    }

    private void finalizeOrder(NodeOrder nodeOrder) {
        log.info("{} : ордер был исполнен по прайсу {}. ", nodeOrder.getOrderId(), nodeOrder.getLimitPrice());
        String message = AssistantMessage.messageProcessing(nodeOrder.getLimitPrice());
        producerServiceExchange.sendAnswer(message, nodeOrder.getNodeUser().getChatId());
        nodeOrder.setOrderState(OrderState.FILLED);
    }

    protected NodeOrder handleFundsExceededException(NodeUser nodeUser, FundsExceededException e) {
        producerServiceExchange.sendAnswer("Не достаточно денег на балансе: " + e.getMessage(), nodeUser.getChatId());
        log.error("Insufficient funds error: {}", e.getMessage());
        tradeStatusManager.stopTrading();
        throw new RuntimeException(e);
    }

    protected NodeOrder handleGeneralException(NodeUser nodeUser, Exception e) {
        producerServiceExchange.sendAnswer("Ошибка: " + e.getMessage(), nodeUser.getChatId());
        log.error("General error: {}", e.getMessage());
        tradeStatusManager.stopTrading();
        throw new RuntimeException(e);
    }

}
