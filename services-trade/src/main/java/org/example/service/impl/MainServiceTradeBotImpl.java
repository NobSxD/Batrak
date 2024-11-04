package org.example.service.impl;

import jakarta.transaction.Transactional;
import org.example.factory.ChangeFactory;
import org.example.service.CreateStrategy;
import org.example.service.MainServiceTradeBot;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.Strategy;
import org.example.xchange.BasicChangeInterface;
import org.knowm.xchange.exceptions.ExchangeException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.example.entity.NodeUser;
import org.example.entity.enams.state.TradeState;

import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * PLACED,        // Ордер был выставлен на продажу или покупку, но еще не исполнен.
 * PARTIALLY_FILLED, // Ордер частично исполнен. Некоторая часть заказа выполнена.
 * COMPLETED,        // Ордер полностью исполнен.
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
    private final CreateStrategy createStrategy;
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
        try {
            if (strategyMap.get(nodeUser.getId()) != null) {
                processServiceCommand.sendAnswer("Трейдинг уже запущен", nodeUser.getChatId());
                return;
            }
            log.info("Прошел проверку на незапущенность трейдинга");
            BasicChangeInterface change = ChangeFactory.createChange(nodeUser);
            if (change == null) {
                nodeUser.setStateTrade(TradeState.TRADE_BASIC);
                processServiceCommand.sendAnswer(
                        "Биржа не найденна: %s, трейденг не запущен".formatted(nodeUser.getChangeType()),
                        nodeUser.getChatId());
                return;
            }
            log.info("Нашел биржу - {}", change.getClass());

            processServiceCommand.sendAnswer("Трейдинг запущен", nodeUser.getChatId());

            Strategy strategy = createStrategy.createStrategy(nodeUser, change);

            if (strategy == null) {
                processServiceCommand.sendAnswer("Не удалось найти стратегию", nodeUser.getChatId());
                return;
            }

            log.info("Создал стратегию  - {}", strategy.getClass());

            strategyMap.put(nodeUser.getId(), strategy);
            Start start = new Start(strategy, nodeUser);
            log.info("Создал клас для многопоточки  - {}", start.getClass());
            start.run();

        } catch (ExchangeException e) {
            handleTradeException(nodeUser, e, "Проверьте правильность введенных данных, " +
                    "API public key, API secret key, или добавьте разрешенный IP сервера");
        } catch (Exception e) {
            handleTradeException(nodeUser, e, "К сожалению, произошла ошибка в процессе торговли. а");
        }
    }

    private void handleTradeException(NodeUser nodeUser, Exception e, String userMessage) {
        strategyMap.remove(nodeUser.getId());
        nodeUser.setStateTrade(TradeState.TRADE_BASIC);
        processServiceCommand.sendAnswer(userMessage, nodeUser.getChatId());
        log.error("Имя пользователя: {}. id: {}. Ошибка: {}.", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
    }

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
            handleTradeException(nodeUser,e,"Проверте правелность введных данных, api public key, api secret key");
        } catch (Exception e) {
            handleTradeException(nodeUser,e,"Получили неожиданную ошибку");
        }
    }

    @Transactional
    @Override
    public void cancelOrder(NodeUser nodeUser) {
        Strategy strategy = strategyMap.get(nodeUser.getId());
        if (checkStrategy(strategy, nodeUser)) {
            strategy.tradeCancel();
        }
    }

    @Override
    public void stopTrade(NodeUser nodeUser) {
        Strategy strategy = strategyMap.get(nodeUser.getId());
        if (checkStrategy(strategy, nodeUser)) {
            strategy.tradeStop();
        }
    }
    private boolean checkStrategy(Strategy strategy, NodeUser nodeUser){
        if (strategy == null){
            processServiceCommand.sendAnswer("Стратегия не была запущенна", nodeUser.getChatId());
            return false;
        } else {
            return true;
        }
    }
     static class Start implements Runnable{
        Strategy strategy;
        NodeUser nodeUser;
        public Start(Strategy strategy, NodeUser nodeUser) {
            this.strategy = strategy;
            this.nodeUser = nodeUser;
        }

        @Override
        public void run() {
            log.info("Запустил метдод strategy.tradeStart(nodeUser) в отдельном потоке  - {}", this.getClass());
            strategy.tradeStart(nodeUser);
        }
    }

}
