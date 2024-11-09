package org.example.strategy;

import org.example.dto.MarketTradeDetails;
import org.example.strategy.impl.helper.TradeStatusManager;

import java.math.BigDecimal;

import org.example.entity.NodeUser;

public interface Strategy {
    void tradeStart(NodeUser nodeUser);
    void tradeCancel();
    void tradeStop();
    BigDecimal currentPrice();

    TradeStatusManager getTradeStatusManager();
    MarketTradeDetails getMarketTradeDetails();
}
