package org.example.strategy.impl.helper;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.enams.state.TradeState;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class TradeStatusManager {
    private volatile TradeState currentTradeState;
    private CountDownLatch countDownLatch;

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
    public void pendingTrading(){
        setCurrentTradeState(TradeState.TRADE_PENDING);
        log.info("Депозид для долнейшей покупки исчерпан, ожидаем возвращение курса");
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

}
