package org.example.strategy.impl;

import io.reactivex.rxjava3.disposables.Disposable;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.NodeOrdersDAO;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.OrderState;
import org.example.entity.enams.state.TradeState;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.Strategy;
import org.example.strategy.impl.helper.AssistantMessage;
import org.example.strategy.impl.helper.AssistantObserver;
import org.example.strategy.impl.helper.TradeStatusManager;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.exceptions.FundsExceededException;

import java.util.List;

@Slf4j
public abstract class StrategyBasic implements Strategy {
    /**
     * Интерфейс для работы с биржей
     */
    protected BasicChangeInterface basicChange;

    /**
     * RabbitMq для отпраки пользователских сообщений
     */
    protected ProcessServiceCommand producerServiceExchange;
    /**
     * Сокет для получение данных с биржи
     */
    protected WebSocketCommand webSocketCommand;

    /**
     * Статус трейдинга
     */
    protected final TradeStatusManager tradeStatusManager;
    protected Disposable disposable;

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
     * @throws IllegalArgumentException если любая из зависимостей сервиса или DAO равна null
     */
    @Transactional
    @Override
    public abstract void tradeStart();

    @Override
    public void tradeStop() {
        tradeStatusManager.stopTrading();
    }
    @Override
    public void tradeCancel() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            tradeStatusManager.cancelTrading();
            log.info("Trading cycle cancelled by user request.");
        }
    }

    @Transactional
    public abstract void process(Order.OrderType orderType, NodeUser nodeUser);

    public void resultTrade(NodeOrdersDAO ordersDAO,NodeUser nodeUser ){
        List<NodeOrder> nodeOrders = ordersDAO.findAllOrdersFromTimestampAndNodeUser( //TODO выбрать только исполненые ордера, учитовать минуты при старте
                nodeUser.getLastStartTread(), nodeUser);
        producerServiceExchange.sendAnswer(
                AssistantMessage.messageResult(
                        AssistantObserver.calculateProfit(nodeOrders),
                        nodeOrders.size()), nodeUser.getChatId());
    }


    protected void finalizeOrder(NodeOrder nodeOrder) {
        log.info("{} : ордер был исполнен по прайсу {}. ", nodeOrder.getOrderId(), nodeOrder.getLimitPrice());
        String message = AssistantMessage.messageProcessing(nodeOrder.getLimitPrice());
        producerServiceExchange.sendAnswer(message, nodeOrder.getNodeUser().getChatId());
        nodeOrder.setOrderState(OrderState.COMPLETED);
    }

    protected void handleFundsExceededException(NodeUser nodeUser, FundsExceededException e) {
        producerServiceExchange.sendAnswer("Не достаточно денег на балансе: " + e.getMessage(), nodeUser.getChatId());
        log.error("Insufficient funds error: {}", e.getMessage());
        tradeStatusManager.stopTrading();
        throw new RuntimeException(e);
    }

    protected void handleGeneralException(NodeUser nodeUser, Exception e) {
        producerServiceExchange.sendAnswer("Ошибка: " + e.getMessage(), nodeUser.getChatId());
        log.error("General error: {}", e.getMessage());
        tradeStatusManager.stopTrading();
        throw new RuntimeException(e);
    }
    protected void handleIllegalArgumentException(NodeUser nodeUser, IllegalArgumentException e){
        producerServiceExchange.sendAnswer("Неверный параметр: " + e.getMessage(), nodeUser.getChatId());
        log.error("Invalid argument for user: {}. Error: {}", nodeUser.getId(), e.getMessage());
        tradeStatusManager.stopTrading();
        throw new RuntimeException(e);
    }
    protected void handleIllegalStateException(NodeUser nodeUser,IllegalStateException e){
        producerServiceExchange.sendAnswer("Неверное состояние объекта: " + e.getMessage(), nodeUser.getChatId());
        log.error("Illegal state for user: {}. Error: {}", nodeUser.getId(), e.getMessage());
        tradeStatusManager.stopTrading();
        throw new RuntimeException(e);
    }

}
