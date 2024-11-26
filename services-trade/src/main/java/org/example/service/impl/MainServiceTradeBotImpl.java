package org.example.service.impl;

import jakarta.transaction.Transactional;
import org.example.dto.NodeUserDto;
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
import java.util.concurrent.CompletableFuture;

import org.example.entity.NodeUser;
import org.example.entity.enams.state.TradeState;

import org.example.dao.NodeUserDAO;

import org.springframework.scheduling.annotation.Async;
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
    private final NodeUserDAO nodeUserDAO;
    private final Map<Long, Strategy> strategyMap = new HashMap<>();


    /**
     * Инициирует процесс трейдинга для указанного пользователя.
     * <p>
     * Метод создает необходимые объекты для взаимодействия с биржей и стратегией
     * для выполнения торговых операций. В случае возникновения ошибок отправляет пользовательские и логгируемые уведомления.
     *
     * @param nodeUserDto объект пользователя, для которого запускается трейдинг
     * @throws ExchangeException если ошибка связана с некорректными данными биржи
     */
    @Override
    @Async
    @Transactional
    public void startTrade(NodeUserDto nodeUserDto) {

        if (checkStrategy(nodeUserDto)) {
            processServiceCommand.sendAnswer("Трейдинг уже запущен", nodeUserDto.getChatId());
            return;
        }

        NodeUser nodeUser = getUser(nodeUserDto);
        try {
            log.info("Прошел проверку на незапущенность трейдинга");

            BasicChangeInterface change = ChangeFactory.createChange(nodeUser);
            handleNPEException(change, "Биржа не найденна: %s, трейденг не запущен".
                    formatted(nodeUser.getChangeType()), nodeUser.getChatId());

            log.info("Нашел биржу - {}", change.getClass());
            Strategy strategy = createStrategy.createStrategy(nodeUser, change);
            handleNPEException(strategy, "Не удалось найти стратегию", nodeUser.getChatId());
            log.info("Создал стратегию  - {}", strategy.getClass());

            strategyMap.put(nodeUser.getId(), strategy);

            CompletableFuture.runAsync(() -> {
                log.info("Запустил метод strategy.tradeStart(nodeUser) в асинхронной задаче - {}", this.getClass());
                processServiceCommand.sendAnswer("Трейдинг запущен", nodeUser.getChatId());
                strategy.tradeStart(nodeUser);
            }).exceptionally(ex -> {
                log.error("Ошибка при выполнении tradeStart: ", ex);
                return null;
            });

            log.info("Завершил метод, отдал RebittMq ответ");

        } catch (ExchangeException e) {
            handleTradeException(nodeUser, e, "Проверьте правильность введенных данных, " +
                    "API public key, API secret key, или добавьте разрешенный IP сервера");
        } catch (Exception e) {
            handleTradeException(nodeUser, e, "К сожалению, произошла ошибка в процессе торговли.");
        }
    }

    private void handleTradeException(NodeUser nodeUser, Exception e, String userMessage) {
        strategyMap.remove(nodeUser.getId());
        nodeUser.setStateTrade(TradeState.TRADE_BASIC);
        processServiceCommand.sendAnswer(userMessage, nodeUser.getChatId());
        log.error("Имя пользователя: {}. id: {}. Ошибка: {}.", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
    }

    private void handleNPEException(Object object, String text, long id) {
        if (object == null) {
            log.error(text);
            processServiceCommand.sendAnswer(text, id);
            throw new RuntimeException();
        }
    }

    @Override
    public void infoAccount(NodeUserDto nodeUserDto) {
        NodeUser nodeUser = nodeUserDAO.findById(nodeUserDto.getId()).orElseThrow();
        try {
            BasicChangeInterface change = ChangeFactory.createChange(nodeUser);
            if (change == null) {
                processServiceCommand.sendAnswer("Аккаунт не найден", nodeUser.getChatId());
                return;
            }
            processServiceCommand.sendAnswer(change.accountInfo(), nodeUser.getChatId());
        } catch (ExchangeException e) {
            handleTradeException(nodeUser, e, "Проверте правелность введных данных, api public key, api secret key");
        } catch (Exception e) {
            handleTradeException(nodeUser, e, "Получили неожиданную ошибку");
        }
    }

    @Transactional
    @Override
    public void cancelOrder(NodeUserDto nodeUser) {
        try {
            Strategy strategy = strategyMap.get(nodeUser.getId());
            if (checkStrategy(strategy, nodeUser)) {
                processServiceCommand.sendAnswer("Отменяю торговлю", nodeUser.getChatId());
                strategy.tradeCancel();
            }
            log.info("Удаляем стратегию из мапы под id: {}, ", nodeUser.getId());
            strategyMap.remove(nodeUser.getId());
        } catch (Exception e) {
            processServiceCommand.sendAnswer(e.getMessage(), nodeUser.getChatId());
            log.error("Во время отмены трейдинга произошла ошибка, {}", e.getMessage());
        }
    }

    @Override
    public void stopTrade(NodeUserDto nodeUser) {
        try {
            Strategy strategy = strategyMap.get(nodeUser.getId());
            if (checkStrategy(strategy, nodeUser)) {
                strategy.tradeStop();
            }
        } catch (Exception e) {
            processServiceCommand.sendAnswer(e.getMessage(), nodeUser.getChatId());
            log.error("Во время остановки трейдинга произошла ошибка, {}", e.getMessage());
        }
    }

    @Override
    public void stateTrade(NodeUserDto nodeUser) {
        try {
            Strategy strategy = strategyMap.get(nodeUser.getId());
            if (checkStrategy(strategy, nodeUser)) {
                String message = """
                        Текущий статус: %s
                        Текущий курс: $%s
                        Прайс на след. покупку: $%s
                        Прайс на след. продажу: $%s
                        Прайс на конечную цену: $%s
                        Последние действие бота: %s
                        """.formatted(
                        strategy.getTradeStatusManager().getCurrentTradeState(),
                        strategy.currentPrice(),
                        strategy.getMarketTradeDetails().getNextBay(),
                        strategy.getMarketTradeDetails().getNexSell(),
                        strategy.getMarketTradeDetails().getEndPrice(),
                        strategy.getMarketTradeDetails().getRecentAction()
                );
                processServiceCommand.sendAnswer(message, nodeUser.getChatId());
            }
        } catch (Exception e) {
            processServiceCommand.sendAnswer(e.getMessage(), nodeUser.getChatId());
            log.error("Во время запроса состояния трейдинга произошла ошибка, {}", e.getMessage());
        }
    }

    private boolean checkStrategy(Strategy strategy, NodeUserDto nodeUser) {
        long nodeId = nodeUser.getId();
        if (strategy == null) {
            processServiceCommand.sendAnswer("Стратегия не была запущенна", nodeUser.getChatId());
            return false;
        }
        TradeState currentTradeState = strategy.getTradeStatusManager().getCurrentTradeState();
        if (TradeState.TRADE_STOP_OK.equals(currentTradeState)) {
            strategyMap.remove(nodeId);
            log.info("Стратегия для пользователя с ID {} была удалена.", nodeId);
            processServiceCommand.sendAnswer("Торговля была завершина, для запроса состояния начните новую торговлю", nodeUser.getChatId());
            return false;
        }
        else {
            return true;
        }
    }

    public boolean checkStrategy(NodeUserDto nodeUser) {
        long nodeId = nodeUser.getId();
        Strategy strategy = strategyMap.get(nodeId);

        if (strategy == null) {
            log.info("Стратегия для пользователя с ID {} не найдена.", nodeId);
            return false;
        }

        TradeState currentTradeState = strategy.getTradeStatusManager().getCurrentTradeState();
        if (TradeState.TRADE_STOP_OK.equals(currentTradeState)) {
            strategyMap.remove(nodeId);
            log.info("Стратегия для пользователя с ID {} была удалена.", nodeId);
            processServiceCommand.sendAnswer("Старая стратегия была удалена, начинаем новую", nodeUser.getChatId());
            return false;
        }

        return true;
    }
    private NodeUser getUser(NodeUserDto nodeUserDto){
        NodeUser nodeUser = nodeUserDAO.findById(nodeUserDto.getId()).orElse(null);
        if (nodeUser == null){
            processServiceCommand.sendAnswer("Пользователь %s не найден".formatted(nodeUserDto.getDisplayName()), nodeUserDto.getChatId());
            throw new RuntimeException();
        }

        return nodeUser;
    }

}
