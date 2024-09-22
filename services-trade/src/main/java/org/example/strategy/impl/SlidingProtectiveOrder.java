package org.example.strategy.impl;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.ConfigTrade;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.OrderState;
import org.example.entity.enams.state.TradeState;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.impl.helper.AssistantMessage;
import org.example.strategy.impl.helper.AssistantObserver;
import org.example.strategy.impl.helper.TradeStatusManager;
import org.example.websocet.WebSocketChange;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.finance.CurrencyConverter;
import org.example.xchange.finance.FinancialCalculator;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.FundsExceededException;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.example.strategy.impl.helper.CreateNodeOrder.createNodeOrder;

/**
 * Класс продукции со свойствами <b>maker</b> и <b>price</b>.
 *
 * @version 0.1
 * @autor Сергей Чесноков
 */

@Slf4j
public class SlidingProtectiveOrder extends StrategyBasic {

    /**
     * RabbitMq для отпраки пользователских сообщений
     */
    private ProcessServiceCommand producerServiceExchange;
    /**
     * Сокет для получение данных с биржи
     */
    private WebSocketCommand webSocketCommand;

    /**
     * Получить пользователя из Cache
     */
    private Cache<Long, NodeUser> nodeUserCache;

    /**
     * Интерфейс для работы с биржей
     */
    private NodeOrder order;
    private final TradeStatusManager tradeStatusManager;
    public SlidingProtectiveOrder(){
        this.tradeStatusManager = new TradeStatusManager();
    }

    public TradeStatusManager getTradeStatusManager() {
        return tradeStatusManager;
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

            // Получение списка ордеров в стакане
            OrderBook orderBook = basicChange.orderBooksLimitOrders(
                    nodeUser.getConfigTrade().getDepthGlass(),
                    nodeUser.getConfigTrade().getNamePair()
            );

            if (tradeStatusManager.getCurrentTradeState().equals(TradeState.BUY)) {
                order = bay(orderBook, nodeUser);
                ordersDAO.save(order);
                nodeUserDAO.save(nodeUser);
            }
            if (tradeStatusManager.getCurrentTradeState().equals(TradeState.SELL)) {
                if (order == null){
                    producerServiceExchange.sendAnswer("Ордер на покупку не был найден, остановите торговлю " +
                            "и попробуйте запустить ее заново", nodeUser.getChatId());
                    throw new  IllegalArgumentException("order cannot be null");
                }
                order = sel(orderBook, nodeUser, order);
                ordersDAO.save(order);
                nodeUserDAO.save(nodeUser);
            }

        }
        List<NodeOrder> nodeOrders = ordersDAO.findAllOrdersFromTimestampAndNodeUser(
                nodeUser.getLastStartTread(), nodeUser);
        producerServiceExchange.sendAnswer(
                AssistantMessage.messageResult(
                        AssistantObserver.calculateProfit(nodeOrders),
                        nodeOrders.size()), nodeUser.getChatId());
    }

    @Transactional
    public NodeOrder bay(OrderBook orderBook, NodeUser nodeUser) {
        if (orderBook == null) {
            throw new IllegalArgumentException("OrderBook cannot be null");
        }
        if (nodeUser == null) {
            throw new IllegalArgumentException("NodeUser cannot be null");
        }
        if (basicChange == null) {
            throw new IllegalArgumentException("BasicChangeInterface cannot be null");
        }

        List<LimitOrder> bids = orderBook.getBids();
        ConfigTrade configTrade = nodeUser.getConfigTrade();
        Instrument currencyPair = new CurrencyPair(configTrade.getNamePair());
        BigDecimal price = FinancialCalculator.maxAmount(bids);
        BigDecimal amount = CurrencyConverter.convertCurrency(price, nodeUser.getConfigTrade().getAmountOrder(),
                configTrade.getScale());
        List<BigDecimal> priceAndAmount = List.of(price, amount);

        try {
            return processOrder(nodeUser, currencyPair, priceAndAmount, Order.OrderType.BID, true);
        } catch (FundsExceededException e) {
            return handleFundsExceededException(nodeUser, e);
        } catch (Exception e) {
            e.printStackTrace();
            return handleGeneralException(nodeUser, e);
        }
    }

    @Transactional
    public NodeOrder sel(OrderBook orderBook, NodeUser nodeUser, NodeOrder nodeOrder) {
        if (orderBook == null) {
            throw new IllegalArgumentException("BasicChangeInterface cannot be null");
        }
        if (nodeUser == null) {
            throw new IllegalArgumentException("BasicChangeInterface cannot be null");
        }
        if (nodeOrder == null) {
            throw new IllegalArgumentException("BasicChangeInterface cannot be null");
        }
        if (basicChange == null) {
            throw new IllegalArgumentException("BasicChangeInterface cannot be null");
        }
        List<LimitOrder> asks = orderBook.getAsks();
        Instrument currencyPair = new CurrencyPair(nodeOrder.getInstrument());
        BigDecimal price = CurrencyConverter.validUsd(FinancialCalculator.maxAmount(asks));
        BigDecimal amount = nodeOrder.getOriginalAmount();
        List<BigDecimal> priceAndAmount = List.of(price, amount);

        try {
            return processOrder(nodeUser, currencyPair, priceAndAmount, Order.OrderType.ASK, false);
        } catch (FundsExceededException e) {
            return handleFundsExceededException(nodeUser, e);
        } catch (Exception e) {
            return handleGeneralException(nodeUser, e);
        }
    }

    @Transactional
    public NodeOrder processOrder(NodeUser nodeUser,
                                  Instrument currencyPair, List<BigDecimal> priceAndAmount, Order.OrderType orderType,
                                  boolean isBid) throws InterruptedException {
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
        if (isBid){
            tradeStatusManager.sell();
        }
        if (!isBid){
            tradeStatusManager.buy();
        }

        return nodeOrder;
    }

    private void initiateWebSocketSubscription(NodeOrder nodeOrder, NodeUser nodeUser, Order.OrderType orderType) {
        WebSocketChange webSocketChange = webSocketCommand.webSocketChange(nodeUser);
        AssistantObserver.subscribeToCurrencyRate(nodeOrder, latch, webSocketChange, orderType);
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

    private NodeOrder handleFundsExceededException(NodeUser nodeUser, FundsExceededException e) {
        producerServiceExchange.sendAnswer("Не достаточно денег на балансе: " + e.getMessage(), nodeUser.getChatId());
        log.error("Insufficient funds error: {}", e.getMessage());
        tradeStatusManager.stopTrading();
        throw new RuntimeException(e);
    }

    private NodeOrder handleGeneralException(NodeUser nodeUser, Exception e) {
        producerServiceExchange.sendAnswer("Ошибка: " + e.getMessage(), nodeUser.getChatId());
        log.error("General error: {}", e.getMessage());
        tradeStatusManager.stopTrading();
        throw new RuntimeException(e);
    }

}
