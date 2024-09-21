package org.example.strategy.impl.helper;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.enams.state.TradeState;

@Slf4j
public class TradeStatusManager {
    private TradeState currentTradeState;

    public TradeStatusManager() {
        this.currentTradeState = TradeState.TRADE_START;
    }

    public TradeState getCurrentTradeState() {
        return currentTradeState;
    }

    private void setCurrentTradeState(TradeState newTradeState) {
        this.currentTradeState = newTradeState;
        log.info("Текущий статус изменен на: {}", newTradeState);
    }

    public void runTrading() {
        setCurrentTradeState(TradeState.TRADE_RUNNING);
        log.info("Торговля в процессе.");
    }

    public void stopTrading() {
        setCurrentTradeState(TradeState.TRADE_STOP);
        log.info("Торговля остановлена.");
    }

    public void cancelTrading() {
        setCurrentTradeState(TradeState.TRADE_CANCEL);
        log.info("Торговля отменена.");
    }

    public void buy() {
        setCurrentTradeState(TradeState.BUY);
        log.info("Ордер на покупку успешно размещен.");
    }
    public void sell() {
        setCurrentTradeState(TradeState.SELL);
        log.info("Ордер на продажу успешно размещен.");
    }


}
