package org.example.strategy;

import org.example.strategy.impl.helper.TradeStatusManager;

public interface Strategy {
    void tradeStart();
    void tradeCancel();
    void tradeStop();

    TradeStatusManager getTradeStatusManager();
}
