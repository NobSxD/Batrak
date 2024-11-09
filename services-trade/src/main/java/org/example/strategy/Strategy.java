package org.example.strategy;

import org.example.strategy.impl.helper.TradeStatusManager;

import org.example.entity.NodeUser;

public interface Strategy {
    void tradeStart(NodeUser nodeUser);
    void tradeCancel();
    void tradeStop();

    TradeStatusManager getTradeStatusManager();
}
