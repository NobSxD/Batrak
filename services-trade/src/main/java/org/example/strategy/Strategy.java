package org.example.strategy;

import org.example.entity.NodeUser;
import org.example.strategy.impl.helper.TradeStatusManager;

public interface Strategy {
    void tradeStart(NodeUser nodeUser);
    void tradeCancel();
    void tradeStop();

    TradeStatusManager getTradeStatusManager();
}
