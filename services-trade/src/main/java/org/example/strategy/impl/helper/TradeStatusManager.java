package org.example.strategy.impl.helper;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

import org.example.entity.enams.state.TradeState;

@Slf4j
public class TradeStatusManager {
    private volatile TradeState currentTradeState;
    private CountDownLatch countDownLatch;
    private boolean stop;

    public TradeStatusManager() {
        stop = false;
        this.currentTradeState = TradeState.TRADE_START;
    }

    public TradeState getCurrentTradeState() {
        if(currentTradeState == null){
            throw new RuntimeException("Состояние трейденга = null");
        }
        return currentTradeState;
    }

    private void setCurrentTradeState(TradeState newTradeState) {
        this.currentTradeState = newTradeState;
        log.info("Текущий статус изменен на: {}", newTradeState);
    }

    public void runTrading() {
        if (!currentTradeState.equals(TradeState.TRADE_RUNNING)) {
            setCurrentTradeState(TradeState.TRADE_RUNNING);
            log.info("Торговля в процессе.");
        }
    }

    public void stopTrading() {
        setStop(true);
        setCurrentTradeState(TradeState.TRADE_STOP);
        log.info("Торговля остановлена.");
    }
    public void startTrading() {
        setCurrentTradeState(TradeState.TRADE_START);
        log.info("Торговля начата.");
    }
    public void pendingTrading(){
        if (!currentTradeState.equals(TradeState.TRADE_PENDING)) {
            setCurrentTradeState(TradeState.TRADE_PENDING);
            log.info("Депозид для долнейшей покупки исчерпан, ожидаем возвращение курса");
        }
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
        setCurrentTradeState(TradeState.TRADE_LAST);
        log.info("Ордер на последнию продажу успешно выполнен. \n" +
                "Цикл завершился, делаю паузу и начинаю новый цикл торговли.");
    }
    public void stopOK(){
        setCurrentTradeState(TradeState.TRADE_STOP_OK);
        log.info("Торговля завершилась по команде TRADE_STOP, при следующем обращение данная стратегия будет удалена.");
    }
    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void countDown(){
        countDownLatch.countDown();
    }

    public void newCountDownLatch(int size) {
        countDownLatch = new CountDownLatch(size);
    }

    public void clearUp(){
        countDownLatch = null;
        currentTradeState = null;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
