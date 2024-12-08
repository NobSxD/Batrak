package org.example.strategy.impl;

import io.reactivex.rxjava3.disposables.Disposable;
import jakarta.transaction.Transactional;
import org.example.dto.NodeDAO;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.Strategy;
import org.example.strategy.impl.helper.AssistantMessage;
import org.example.strategy.impl.helper.TradeStatusManager;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.DTO.WalletDTO;
import org.example.xchange.finance.CurrencyConverter;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.exceptions.FundsExceededException;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;

import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;

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
    protected TradeStatusManager tradeStatusManager;

    protected Disposable disposable;
    protected WalletDTO walletDTO;
    protected NodeDAO nodeDAO;

    protected StrategyBasic(TradeStatusManager tradeStatusManager, NodeOrdersDAO nodeOrdersDAO, NodeUserDAO nodeUserDAO) {
        nodeDAO = new NodeDAO(nodeOrdersDAO, nodeUserDAO);
        this.tradeStatusManager = tradeStatusManager;
    }

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
    public abstract NodeOrder process(Order.OrderType orderType, NodeUser nodeUser);

    public void resultTrade(WalletDTO walletDTO, NodeUser nodeUser, BigDecimal currentPrice) {
        BigDecimal coinInUsdt = walletDTO.getCoin(currentPrice);
        BigDecimal resultDeposit = walletDTO.getDeposit().subtract(walletDTO.getFirstDeposit()).add(coinInUsdt);
        coinInUsdt = CurrencyConverter.validScale(coinInUsdt);
        resultDeposit = CurrencyConverter.validScale(resultDeposit);

        producerServiceExchange.sendAnswer(
                AssistantMessage.messageResult(resultDeposit, coinInUsdt, walletDTO.getCountDelay(),
                        walletDTO.getCurrencyPair().base.getDisplayName()), nodeUser.getChatId());
    }


    protected void finalizeOrder(NodeOrder nodeOrder) {
        log.info("id: {}, ордер был исполнен по прайсу {}. ", nodeOrder.getOrderId(), nodeOrder.getLimitPrice());
        String message = AssistantMessage.messageProcessing(nodeOrder);
        message = message + walletDTO.toString();
        producerServiceExchange.sendAnswer(message, nodeOrder.getNodeUser().getChatId());
    }

    protected void handleFundsExceededException(NodeUser nodeUser, FundsExceededException e) {
        producerServiceExchange.sendAnswer("Не достаточно денег на балансе: " + e.getMessage(), nodeUser.getChatId());
        log.error("Insufficient funds error: {}", e.getMessage());
        tradeStatusManager.stopTrading();
        tradeStatusManager.stopOK();
        throw new RuntimeException(e);
    }

    protected void handleGeneralException(NodeUser nodeUser, Exception e) {
        producerServiceExchange.sendAnswer("Ошибка: " + e.getMessage(), nodeUser.getChatId());
        log.error("General error: {}", e.getMessage());
        tradeStatusManager.stopTrading();
        tradeStatusManager.stopOK();
        throw new RuntimeException(e);
    }

    protected void handleIllegalArgumentException(NodeUser nodeUser, IllegalArgumentException e) {
        producerServiceExchange.sendAnswer("Неверный параметр: " + e.getMessage(), nodeUser.getChatId());
        log.error("Invalid argument for user: {}. Error: {}", nodeUser.getId(), e.getMessage());
        tradeStatusManager.stopTrading();
        tradeStatusManager.stopOK();
        throw new RuntimeException(e);
    }

}
