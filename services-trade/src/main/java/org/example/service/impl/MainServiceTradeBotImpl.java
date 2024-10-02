package org.example.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.OrderState;
import org.example.entity.enams.state.TradeState;
import org.example.factory.ChangeFactory;
import org.example.factory.CreateStrategy;
import org.example.service.MainServiceTradeBot;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.Strategy;
import org.example.strategy.impl.helper.TradeStatusManager;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;
import org.knowm.xchange.exceptions.ExchangeException;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PLACED,        // Ордер был выставлен на продажу или покупку, но еще не исполнен.
 * PARTIALLY_FILLED, // Ордер частично исполнен. Некоторая часть заказа выполнена.
 * FILLED,        // Ордер полностью исполнен.
 * CANCELLED,     // Ордер был отменен пользователем или системой.
 * PENDING_CANCEL, // Запрос на отмену ордера инициирован, но еще не подтвержден.
 * EXPIRED,       // Срок действия ордера истек до его исполнения.
 * REJECTED       // Ордер был отклонен системой из-за ошибок или неправильных данных.
 * <p>
 * <p>
 * Основная реализация интерфейса MainServiceTradeBot для управления процессом торговли.
 * <p>
 * Класс отвечает за инициацию и управление торговыми стратегиями на разных биржах,
 * обеспечивает взаимодействие с сервисами и вебсокетами для осуществления трейдинга.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MainServiceTradeBotImpl implements MainServiceTradeBot {
    private final ProcessServiceCommand processServiceCommand;
    private final Cache<Long, NodeUser> nodeUserCache; //TODO продумать как сбрасывать кешь
    private final NodeUserDAO nodeUserDAO;
    private final NodeOrdersDAO nodeOrdersDAO;
    private final WebSocketCommand webSocketCommand;
    private final Map<Long, Strategy> strategyMap = new HashMap<>();


    /**
     * Инициирует процесс трейдинга для указанного пользователя.
     * <p>
     * Метод создает необходимые объекты для взаимодействия с биржей и стратегией
     * для выполнения торговых операций. В случае возникновения ошибок отправляет пользовательские и логгируемые уведомления.
     *
     * @param nodeUser объект пользователя, для которого запускается трейдинг
     * @throws ExchangeException если ошибка связана с некорректными данными биржи
     */
    @Override
    @Transactional
    public void startTrade(@Payload NodeUser nodeUser) {
        Strategy strategy;
        BasicChangeInterface change;
        try {
            if (strategyMap.get(nodeUser.getId()) != null){
                processServiceCommand.sendAnswer("Трейдинг уже запущен", nodeUser.getChatId());
                return;
            }
            change = ChangeFactory.createChange(nodeUser);
            if (change == null) {
                nodeUser.setStateTrade(TradeState.TRADE_BASIC);
                processServiceCommand.sendAnswer("Биржа не найденна: %s, трейденг не запущен".formatted(nodeUser.getMenuChange()), nodeUser.getChatId());
                return;
            }
            processServiceCommand.sendAnswer("Трейдинг запущен", nodeUser.getChatId());
            if (nodeUser.getStateTrade().equals(TradeState.SELL)){
                strategy = strategyMap.get(nodeUser.getId());
            }else {
                strategy = CreateStrategy.createStrategy(nodeUser);
            }

            if (strategy == null) {
                processServiceCommand.sendAnswer("Не удалось найти стратегию", nodeUser.getChatId());
                return;
            }
            strategyMap.put(nodeUser.getId(), strategy);
            strategy.trade(nodeUser, processServiceCommand, webSocketCommand, nodeUserDAO, nodeOrdersDAO, nodeUserCache, change);
            strategyMap.remove(nodeUser.getId());

        } catch (ExchangeException e) {
            strategyMap.remove(nodeUser.getId());
            processServiceCommand.sendAnswer("Проверте правелность введных данных, api public key, api secret key, или добавте разрешенный ip server", nodeUser.getChatId());
            log.error("Имя пользователя: {}. id: {}. Ошибка: {}.", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
        } catch (Exception e) {
            strategyMap.remove(nodeUser.getId());
            nodeUser.setStateTrade(TradeState.TRADE_BASIC);
            log.error("Имя пользователя: {}. id: {}. Ошибка: {}.", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
        }
    }


    /**
     * Возвращает информацию об аккаунте для указанного пользователя.
     * <p>
     * Метод взаимодействует с интерфейсом изменения для получения и отправки информации об аккаунте
     * пользователю через сервис команд. В случае возникновения ошибок отправляет пользовательские
     * и логгируемые уведомления.
     *
     * @param nodeUser объект пользователя, для которого запрашивается информация об аккаунте
     */
    @Override
    public void infoAccount(NodeUser nodeUser) {
        try {
            BasicChangeInterface change = ChangeFactory.createChange(nodeUser);
            if (change == null) {
                processServiceCommand.sendAnswer("Аккаунт не найден", nodeUser.getChatId());
                return;
            }
            processServiceCommand.sendAnswer(change.accountInfo(), nodeUser.getChatId());
        } catch (ExchangeException e) {
            nodeUser.setStateTrade(TradeState.TRADE_BASIC);
            processServiceCommand.sendAnswer("Проверте правелность введных данных, api public key, api secret key", nodeUser.getChatId());
            log.error("{} имя пользователя:{}. Id пользователя {}", e.getMessage(), nodeUser.getUsername(), nodeUser.getId());
        } catch (Exception e) {
            nodeUser.setStateTrade(TradeState.TRADE_BASIC);
            log.error("{} имя пользователя:{}. Id пользователя {}", e.getMessage(), nodeUser.getUsername(), nodeUser.getId());
        }
    }

    /**
     * Отменяет последний ордер для указанного пользователя, если он доступен.
     * <p>
     * Метод извлекает пользователя из кеша, проверяет наличие ордеров и, если стратегия
     * активна, отменяет последний ордер. Если операция невозможна, отправляет
     * соответствующие уведомления пользователю.
     *
     * @param nodeUser объект пользователя, для которого отменяется ордер
     * @throws IllegalStateException если ордер или стратегия не найдены
     */
    @Transactional
    @Override
    public void cancelOrder(NodeUser nodeUser) {
        NodeUser nodeUserCache = this.nodeUserCache.getIfPresent(nodeUser.getId());

        // Обработка случая, когда пользователь не найден в кеше
        if (nodeUserCache == null) {
            log.warn("User not found in cache, cannot cancel order for userId={}", nodeUser.getId());
            processServiceCommand.sendAnswer("Ордер для отмены не найден", nodeUser.getChatId());
            return;
        }

        // Обработка случая, когда ордера отсутствуют
        List<NodeOrder> orders = nodeUserCache.getOrders();
        if (orders == null || orders.isEmpty()) {
            log.warn("No orders found for userId={}", nodeUser.getId());
            processServiceCommand.sendAnswer("Ордер для отмены не найден", nodeUser.getChatId());
            return;
        }

        // Обработка отсутствия стратегии
        Strategy strategy = strategyMap.get(nodeUser.getId());
        if (strategy == null) {
            processServiceCommand.sendAnswer("Стратегия не была запущенна", nodeUser.getChatId());
            return;
        }

        // Попытка отменить последний ордер
        try {
            NodeOrder nodeOrder = orders.get(orders.size() - 1);
            if (nodeOrder == null) {
                log.warn("No last orders found for userId={}", nodeUser.getId());
                processServiceCommand.sendAnswer("Последний ордер для отмены не был найден", nodeUser.getChatId());
                return;
            }

            try {
                nodeOrder.setOrderState(OrderState.PENDING_CANCEL);
                strategy.cancelOrder(nodeOrder);

                log.info("Order with id={} is set to pending cancel state for userId={}", nodeOrder.getId(), nodeUser.getId());
            } catch (ExchangeException e) {
                handleOrderCancellationException(nodeUser,nodeOrder, "Ордер не найден", e);
            } catch (Exception e) {
                handleOrderCancellationException(nodeUser,nodeOrder, "Неожиданная ошибка при отмене ордера", e);
            }

        } catch (IndexOutOfBoundsException e) {
            log.warn("Attempt to access non-existent order for userId={}", nodeUser.getId());
            processServiceCommand.sendAnswer("Ошибка доступа к ордеру", nodeUser.getChatId());
        }
    }

    private void handleOrderCancellationException(NodeUser nodeUser, NodeOrder nodeOrder, String userMessage, Exception e) {
        if (nodeOrder != null) {
            nodeOrder.setOrderState(OrderState.REJECTED);
        }
        if (e instanceof ExchangeException) {
            log.error("Exchange error while cancelling order: {}", e.getMessage());
        } else {
            log.error("Unexpected error: {}", e.getMessage(), e);
        }
        processServiceCommand.sendAnswer(userMessage, nodeUser != null ? nodeUser.getChatId() : null);
    }

    @Override
    public void stopTrade(NodeUser nodeUser){
        Strategy strategy = strategyMap.get(nodeUser.getId());
        if (strategy == null){
            processServiceCommand.sendAnswer("Стратегия не была запущенна", nodeUser.getChatId());
            return;
        }

        TradeStatusManager tradeStatusManager = strategy.getTradeStatusManager();
        if (tradeStatusManager.getCurrentTradeState().equals(TradeState.BUY)){
            cancelOrder(nodeUser); //если ордер выстовлен на покупку, то мы можем его отменить и остановить трейдинг без потерь
        } else{
            tradeStatusManager.stopTrading();
        }

    }
}
