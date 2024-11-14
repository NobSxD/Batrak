package org.example.strategy.impl;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.transaction.Transactional;
import org.example.dto.MarketTradeDetails;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.impl.helper.TradeStatusManager;
import org.example.websocet.WebSocketChange;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.finance.CurrencyConverter;
import org.example.xchange.finance.FinancialCalculator;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.exceptions.FundsExceededException;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.OrderState;
import org.example.entity.enams.state.TradeState;

import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;

/**
 * @params buyLevels - сетка ордеров.
 * @params endPrice - цена при которой нужно завершить цыкл.
 * @params currentPrice - текущий прайс валюты.
 * @params lastBuyPrice - последния цена покупки.
 * @params nPercent - процент шага, при достижении которого делаеться покупка или продажа.
 * @params instrument - валютная пара.
 * @params coinAmount - сумма в $ на покупку coin, в долнейшем будет конвертирован в криптовалюту .
 * @params demoTrade - включить ли виртуальныю торговлю.
 * @params scale - точность валюты, данный параметр важен для отправки ордера на биржу.
 */
@Slf4j
public class GridTrading extends StrategyBasic {

    private MarketTradeDetails marketTradeDetails;
    private BigDecimal currentPrice;

    public GridTrading(NodeUser nodeUser, BasicChangeInterface basicChange, NodeUserDAO nodeUserDAO,
                       WebSocketCommand webSocketCommand, ProcessServiceCommand producerServiceExchange,
                       NodeOrdersDAO nodeOrdersDAO) {
        super(new TradeStatusManager(), nodeOrdersDAO, nodeUserDAO);
        marketTradeDetails = new MarketTradeDetails(
                new CurrencyPair(nodeUser.getConfigTrade().getNamePair()),
                nodeUser.getConfigTrade().getAmountOrder(),
                nodeUser.getConfigTrade().getStepSellD(),
                nodeUser.getConfigTrade().getStepBayD(),
                nodeUser.getConfigTrade().getScale()
        );
        marketTradeDetails.setMaxCountDeal(nodeUser.getConfigTrade().getDeposit().intValue(),
                nodeUser.getConfigTrade().getAmountOrder().intValue());
        this.basicChange = basicChange;
        this.webSocketCommand = webSocketCommand;
        this.producerServiceExchange = producerServiceExchange;
        tradeStatusManager.startTrading();
        tradeStatusManager.newCountDownLatch(1);
    }


    @Override
    public TradeStatusManager getTradeStatusManager() {
        return tradeStatusManager;
    }

    @Override
    @Transactional
    public synchronized void tradeStart(NodeUser nodeUser) {
        try {
            while (true) {
                if (tradeStatusManager.getCurrentTradeState() == TradeState.TRADE_STOP) {
                    log.info("Поступила команда на остановку трейдинга");
                    break;
                }

                log.info("Начинаю торговлю в методе startTradingCycle");
                startTradingCycle(nodeUser);
                log.info("Блокирую поток пока метод startTradingCycle не разблокирует его");
                awaitLatch(tradeStatusManager.getCountDownLatch());

                if (tradeStatusManager.getCurrentTradeState() == TradeState.TRADE_CANCEL) {
                    log.info("Поступила команда на отмену, поток разблокирован, торговля отменена");
                    break;
                }
                // Подождите 40 секунд перед следующим циклом
                try {
                    tradeStatusManager.sellLast();
                    log.info("Достигнута конечная цена, завершаем торговый цикл. : {}", marketTradeDetails.getLastPrice());
                    log.info("""
                                    Цикл завершился, ожидаем 40 секунда и начинаем новую торговлю,
                                    tradeStatusManager: {}
                                    lastPrice : {}
                                    endPrice: {}""",
                            tradeStatusManager.getCurrentTradeState(), marketTradeDetails.getLastPrice(),
                            marketTradeDetails.getEndPrice());
                    marketTradeDetails.setLastPrice(null);
                    marketTradeDetails.setEndPrice(null);
                    Thread.sleep(40000);
                } catch (InterruptedException e) {
                    log.error("Поток был прерван: ", e);
                    Thread.currentThread().interrupt(); // Восстановление прерывания
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Ошибка в торговом цикле: ", e);
        } finally {
            tradeStatusManager.stopOK();
            resultTrade(nodeUser);
            cleanUp();
        }
    }

    @Override
    public BigDecimal currentPrice() {
        return CurrencyConverter.validUsd(currentPrice);
    }


    @Transactional
    public void startTradingCycle(NodeUser nodeUser) {
        WebSocketChange webSocketChange = webSocketCommand.webSocketChange(nodeUser);
        disposable = webSocketChange.exchangePair(marketTradeDetails.getInstrument())
                .observeOn(Schedulers.single())
                .subscribe(rate -> {
                    NodeOrder nodeOrder = processPrice(rate, nodeUser);
                    currentPrice = rate;
                    if (nodeOrder != null) {
                        finalizeOrder(nodeOrder);
                        nodeDAO.nodeOrdersDAO().save(nodeOrder);
                        nodeDAO.nodeUserDAO().save(nodeUser);
                    }
                }, error -> {
                    log.error("Error occurred: {}", error.getMessage());
                });
    }


    @Transactional
    public NodeOrder processPrice(BigDecimal currentPrice, NodeUser nodeUser) {
        NodeOrder nodeOrder = null;
        if (marketTradeDetails.getCountDeal() >= marketTradeDetails.getMaxCountDeal()) {
            log.info("1.Превышен лимит:  максимальное количество сделок на покупку {}, уже сделанно ордеров на покупку {}",
                    marketTradeDetails.getMaxCountDeal(), marketTradeDetails.getCountDeal());
        } else if (tradeStatusManager.getCurrentTradeState().equals(TradeState.TRADE_START)) {
            nodeOrder = executeBuyOrder(currentPrice, nodeUser);
            marketTradeDetails.setEndPrice(FinancialCalculator.increaseByPercentage(currentPrice, marketTradeDetails.getStepSell()));
            marketTradeDetails.setNextBay(FinancialCalculator.subtractPercentage(currentPrice, marketTradeDetails.getStepBay()));
            marketTradeDetails.setNexSell(FinancialCalculator.addPercentage(marketTradeDetails.getLastPrice(), marketTradeDetails.getStepSell()));
            marketTradeDetails.setRecentAction("FIRST_BAY");
            tradeStatusManager.runTrading();
            log.info("1.FIRST_BAY: bayPrice {}, endPrice: {}, nextBay: {}, nextSell: {}, nodeOrderPrice: {} ",
                    currentPrice, marketTradeDetails.getEndPrice(), marketTradeDetails.getNextBay(),
                    marketTradeDetails.getNexSell(), nodeOrder.getLimitPrice());
        } else if (shouldLastSell(currentPrice)) {
            nodeOrder = executeSellOrder(currentPrice, nodeUser);
            marketTradeDetails.setRecentAction("LAST_SELL");
            tradeStatusManager.getCountDownLatch().countDown();
            dispose(disposable);
            log.info("2.LAST_SELL: sellPrice: {}, nodeOrderPrice: {} ", currentPrice, nodeOrder.getLimitPrice());
        } else if (shouldBuy(currentPrice)) {
            nodeOrder = executeBuyOrder(currentPrice, nodeUser);
            marketTradeDetails.setNextBay(FinancialCalculator.subtractPercentage(currentPrice, marketTradeDetails.getStepBay()));
            marketTradeDetails.setNexSell(FinancialCalculator.addPercentage(marketTradeDetails.getLastPrice(), marketTradeDetails.getStepSell()));
            marketTradeDetails.setRecentAction("BAY");
            log.info("3.BAY: bayPrice: {}, nextBay: {}, nextSell: {}, nodeOrderPrice: {} ",
                    currentPrice, marketTradeDetails.getNextBay(), marketTradeDetails.getNexSell(), nodeOrder.getLimitPrice());
        } else if (shouldSell(currentPrice)) {
            nodeOrder = executeSellOrder(currentPrice, nodeUser);
            marketTradeDetails.setNextBay(FinancialCalculator.subtractPercentage(currentPrice, marketTradeDetails.getStepBay()));
            marketTradeDetails.setNexSell(FinancialCalculator.addPercentage(marketTradeDetails.getLastPrice(), marketTradeDetails.getStepSell()));
            marketTradeDetails.setRecentAction("SELL");
            log.info("4.SELL: sellPrice {} nextBay: {}, nextSell: {}, nodeOrderPrice: {} ",
                    currentPrice, marketTradeDetails.getNextBay(), marketTradeDetails.getNexSell(), nodeOrder.getLimitPrice());
        }
        return nodeOrder;
    }

    public boolean shouldBuy(BigDecimal currentPrice) {
        boolean count = marketTradeDetails.getCountDeal() <= marketTradeDetails.getMaxCountDeal();
        boolean canBuy = currentPrice.compareTo(marketTradeDetails.getNextBay()) <= 0;
        if (!count) {
            tradeStatusManager.pendingTrading();
        }
        if (canBuy && count) {
            log.info("shouldSell: currentPrice - nextBay = {} <= {}, lastPrice = {}, getCountDeal {}, getMaxCountDeal {}",
                    currentPrice, marketTradeDetails.getNextBay(), marketTradeDetails.getLastPrice(),
                    marketTradeDetails.getCountDeal(), marketTradeDetails.getMaxCountDeal());
            return true;
        }
        return false;
    }

    public boolean shouldSell(BigDecimal currentPrice) {
        boolean count = marketTradeDetails.getCountDeal() > 1;
        boolean canSell = currentPrice.compareTo(marketTradeDetails.getNexSell()) >= 0;
        if (canSell && count) {
            tradeStatusManager.runTrading();
            log.info("shouldSell: currentPrice - nexSell = {} >= {}, lastPrice = {}, count {} ",
                    currentPrice, marketTradeDetails.getNexSell(), marketTradeDetails.getLastPrice()
                    , marketTradeDetails.getCountDeal());
            return true;
        }
        return false;
    }

    public boolean shouldLastSell(BigDecimal currentPrice) {
        boolean count = marketTradeDetails.getCountDeal() == 1;
        boolean canSell = marketTradeDetails.getEndPrice().compareTo(currentPrice) <= 0;
        if (canSell && count) {
            tradeStatusManager.runTrading();
            log.info("SELL LAST:  endPrice - currentPrice = {} <= {}, count {}  ", marketTradeDetails.getEndPrice(),
                    currentPrice, marketTradeDetails.getCountDeal());
            return true;
        }
        return false;
    }

    public NodeOrder executeBuyOrder(BigDecimal price, NodeUser nodeUser) {
        marketTradeDetails.setCountDeal(marketTradeDetails.getCountDeal() + 1);
        marketTradeDetails.setLastPrice(price);
        return process(Order.OrderType.BID, nodeUser);
    }

    public NodeOrder executeSellOrder(BigDecimal price, NodeUser nodeUser) {
        marketTradeDetails.setCountDeal(marketTradeDetails.getCountDeal() - 1);
        marketTradeDetails.setLastPrice(price);
        return process(Order.OrderType.ASK, nodeUser);
    }

    public NodeOrder process(Order.OrderType orderType, NodeUser nodeUser) {
        try {
            BigDecimal amount = marketTradeDetails.getCoinAmount();
            if (orderType.equals(Order.OrderType.ASK)) {
                amount = marketTradeDetails.getSell();
            }

            MarketOrder marketOrder = basicChange.createMarketOrder(orderType, amount, marketTradeDetails.getLastPrice(),
                    marketTradeDetails.getScale(), marketTradeDetails.getInstrument());

            String idOrder = basicChange.placeMarketOrder(marketOrder);

            return NodeOrder.builder()
                    .limitPrice(marketTradeDetails.getLastPrice())
                    .type(orderType.name())
                    .orderId(idOrder)
                    .orderState(OrderState.COMPLETED)
                    .originalAmount(CurrencyConverter.convertCurrency(marketTradeDetails.getLastPrice(), amount, marketTradeDetails.getScale()))
                    .usd(amount)
                    .timestamp(new Date())
                    .instrument(nodeUser.getConfigTrade().getNamePair())
                    .checkReal(nodeUser.getConfigTrade().isEnableDemoTrading())
                    .nodeUser(nodeUser)
                    .menuStrategy(nodeUser.getConfigTrade().getStrategy())
                    .build();
        } catch (FundsExceededException e) {
            handleFundsExceededException(nodeUser, e);
        } catch (IllegalArgumentException e) {
            handleIllegalArgumentException(nodeUser, e);
        } catch (IllegalStateException e) {
            handleIllegalStateException(nodeUser, e);
        } catch (Exception e) {
            handleGeneralException(nodeUser, e);
        }
        return null;
    }
    public void dispose(Disposable disposable){
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void awaitLatch(CountDownLatch latch) {
        try {
            latch.await(); // Блокируем текущий поток до завершения задачи
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Восстановление статуса прерывания
            log.error("Торговый поток был прерван:", e);
        }
    }

    private void cleanUp() {
        tradeStatusManager.clearUp();
        tradeStatusManager = null;
        nodeDAO = null;
        marketTradeDetails = null;
        basicChange = null;
        producerServiceExchange = null;
        webSocketCommand = null;
        disposable = null;
    }

    public MarketTradeDetails getMarketTradeDetails() {
        return marketTradeDetails;
    }
}
