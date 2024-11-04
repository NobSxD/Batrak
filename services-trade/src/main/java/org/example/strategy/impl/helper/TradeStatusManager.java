package org.example.strategy.impl.helper;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

import org.example.entity.enams.state.TradeState;

@Slf4j
public class TradeStatusManager {
    private volatile TradeState currentTradeState;
    public CountDownLatch countDownLatch;

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
    public void startTrading() {
        setCurrentTradeState(TradeState.TRADE_START);
        log.info("Торговля начата.");
    }

    public void cancelTrading() {
        setCurrentTradeState(TradeState.TRADE_CANCEL);
        countDownLatch.countDown();
        log.info("Торговля отменена.");
    }

    public void buy() {
        setCurrentTradeState(TradeState.BUY);
        log.info("Ордер на покупку успешно выполнен.");
    }
    public void sell() {
        setCurrentTradeState(TradeState.SELL);
        log.info("Ордер на продажу успешно выполнен.");
    }
    public void sellLast() {
        setCurrentTradeState(TradeState.TRADE_START);
        countDownLatch.countDown();
        log.info("Ордер на продажу успешно выполнен. \n" +
                "Цикл завершился, делаю паузу и начинаю новый цикл торговли.");
    }
    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void newCountDownLatch(int size) {
        countDownLatch = new CountDownLatch(size);
    }

    public void clearUp(){
        countDownLatch = null;
        currentTradeState = null;
    }

}
