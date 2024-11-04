package org.example.strategy.impl;

import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.transaction.Transactional;
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
import org.knowm.xchange.instrument.Instrument;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
    public final Set<BigDecimal> buyLevels = new HashSet<>();
    private final Instrument instrument;
    private final BigDecimal coinAmount;
    private final double stepSell;
    private final double stepBay;
    private final int scale;
    BigDecimal endPrice;
    BigDecimal nextBay;
    BigDecimal nexSell;
    BigDecimal lastPrice;

    public GridTrading(NodeUser nodeUser, BasicChangeInterface basicChange, NodeUserDAO nodeUserDAO,
                       WebSocketCommand webSocketCommand, ProcessServiceCommand producerServiceExchange,
                       NodeOrdersDAO nodeOrdersDAO) {
        super(new TradeStatusManager(), nodeOrdersDAO, nodeUserDAO);
        this.basicChange = basicChange;
        this.webSocketCommand = webSocketCommand;
        this.producerServiceExchange = producerServiceExchange;
        this.instrument = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
        this.coinAmount = nodeUser.getConfigTrade().getAmountOrder();
        this.scale = nodeUser.getConfigTrade().getScale();
        this.stepBay = nodeUser.getConfigTrade().getStepBayD();
        this.stepSell = nodeUser.getConfigTrade().getStepSellD();
        tradeStatusManager.startTrading();
        tradeStatusManager.newCountDownLatch(1);
    }


    @Override
    public TradeStatusManager getTradeStatusManager() {
        return tradeStatusManager;
    }

    @Override
    @Transactional
    public void tradeStart(NodeUser nodeUser) {
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
                    resultTrade(nodeUser);
                    break;
                }
                // Подождите 40 секунд перед следующим циклом
                try {
                    log.info("Достигнута конечная цена, завершаем торговый цикл. : {}", lastPrice);
                    tradeStatusManager.sellLast();
                    lastPrice = null;
                    endPrice = null;
                    StringBuilder sb = new StringBuilder();

                    for (BigDecimal level : buyLevels) {  //TODO после отладки удалить
                        sb.append("Buy Level: ").append(level.toString()).append("\n");
                    }
                    log.info("Цикл завершился, ожидаем 40 секунда и начинаем новую торговлю, \n"
                                    + "tradeStatusManager: {} \n"
                                    + "lastPrice : {} \n"
                                    + "endPrice: {}\n"
                                    + "buyLevels : {}",
                            tradeStatusManager.getCurrentTradeState(), lastPrice, endPrice, sb);
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
            cleanUp();
            tradeStatusManager.clearUp();
        }
    }


    @Transactional
    public void startTradingCycle(NodeUser nodeUser) {
        WebSocketChange webSocketChange = webSocketCommand.webSocketChange(nodeUser);
        disposable = webSocketChange.exchangePair(instrument)
                .observeOn(Schedulers.io())
                .subscribe(rate -> {
                    NodeOrder nodeOrder = processPrice(rate, nodeUser);
                    if (nodeOrder != null) {
                        if (nodeUser.getOrders() == null) {
                            nodeUser.setOrders(new ArrayList<>());
                        }
                        nodeUser.getOrders().add(nodeOrder);
                        finalizeOrder(nodeOrder);
                        nodeUserDAO.save(nodeUser);
                    }
                }, error -> {
                    log.error("Error occurred: {}", error.getMessage());
                });
    }


    public NodeOrder processPrice(BigDecimal currentPrice, NodeUser nodeUser) {
        NodeOrder nodeOrder = null;
        if (tradeStatusManager.getCurrentTradeState().equals(TradeState.TRADE_START)) {
            nodeOrder = executeBuyOrder(currentPrice, nodeUser);
            endPrice = FinancialCalculator.increaseByPercentage(currentPrice, stepSell);
            nextBay = FinancialCalculator.subtractPercentage(currentPrice, stepBay);
            tradeStatusManager.runTrading();
            log.info("FIRST_BAY: bayPrice {}, endPrice: {}, nextBay: {}, nextSell: {} ", currentPrice, endPrice, nextBay, nexSell);
        }
        if (shouldBuy(currentPrice)) {
            nodeOrder = executeBuyOrder(currentPrice, nodeUser);
            nextBay = FinancialCalculator.subtractPercentage(currentPrice, stepBay);
            nexSell = FinancialCalculator.addPercentage(lastPrice, stepSell);
            log.info("BAY: bayPrice: {}, nextBay: {}, nextSell: {} ", currentPrice, nextBay, nexSell);
        } else if (shouldSell(currentPrice)) {
            nodeOrder = executeSellOrder(currentPrice, nodeUser);
            nextBay = FinancialCalculator.subtractPercentage(currentPrice, stepBay);
            nexSell = FinancialCalculator.addPercentage(lastPrice, stepSell);
            log.info("SELL: sellPrice {} nextBay: {}, nextSell: {} ", currentPrice, nextBay, nexSell);
        } else if (shouldLastSell(currentPrice)) {
            nodeOrder = executeSellOrder(currentPrice, nodeUser);
            tradeStatusManager.getCountDownLatch().countDown();
            log.info("LAST_SELL: sellPrice: {}", currentPrice);
        }
        return nodeOrder;
    }

    public boolean shouldBuy(BigDecimal currentPrice) {
        boolean canBuy = currentPrice.compareTo(nextBay) <= 0;
        boolean contains = !buyLevels.contains(currentPrice);
        if (canBuy && contains) {
            log.info("BAY: currentPrice = {}, targetBuyPrice = {}, buyLevels = {} ", currentPrice, nextBay, buyLevels);
        }
        return canBuy && contains;
    }

    public boolean shouldSell(BigDecimal currentPrice) {
        boolean canSell = currentPrice.compareTo(nextBay) >= 0;
        boolean contains = buyLevels.contains(lastPrice);
        if (canSell && contains) {
            log.info("SELL: currentPrice = {}, targetBuyPrice = {}, buyLevels = {} ", currentPrice, nextBay, buyLevels);
        }
        return canSell && contains;
    }

    public boolean shouldLastSell(BigDecimal currentPrice) {
        boolean canSell = endPrice.compareTo(currentPrice) <= 0;
        boolean contains = buyLevels.contains(lastPrice);
        if (canSell && contains) {
            log.info("SELL LAST: currentPrice = {}, endPrice = {}, buyLevels = {} ", currentPrice, endPrice, buyLevels);
        }
        return canSell && contains;
    }

    public NodeOrder executeBuyOrder(BigDecimal price, NodeUser nodeUser) {
        lastPrice = price;
        buyLevels.add(price);
        StringBuilder sb = new StringBuilder();
        for (BigDecimal level : buyLevels) {
            sb.append("Buy Level: ").append(level.toString()).append("\n");
        }
        log.info("price = {} \n buyLevels = {}", price, sb);
        return process(Order.OrderType.BID, nodeUser);
    }

    public NodeOrder executeSellOrder(BigDecimal price, NodeUser nodeUser) {
        buyLevels.remove(lastPrice);
        lastPrice = price;
        StringBuilder sb = new StringBuilder();
        for (BigDecimal level : buyLevels) {
            sb.append("Buy Level: ").append(level.toString()).append("\n");
        }
        log.info("price = {} \n buyLevels = {}", price, sb);
        return process(Order.OrderType.ASK, nodeUser);
    }

    public NodeOrder process(Order.OrderType orderType, NodeUser nodeUser) {
        try {
            BigDecimal amount = coinAmount;
            if (orderType.equals(Order.OrderType.ASK)) {
                amount = FinancialCalculator.increaseByPercentage(coinAmount, stepSell);
            }
            BigDecimal cryptoQty = CurrencyConverter.convertCurrency(lastPrice, amount, scale);

            MarketOrder marketOrder = basicChange.createMarketOrder(orderType, cryptoQty, instrument);
            if (marketOrder == null) {
                throw new IllegalStateException("Failed to create market order");
            }

            String idOrder = basicChange.placeMarketOrder(marketOrder);

            return NodeOrder.builder()
                    .limitPrice(lastPrice)
                    .type(orderType.name())
                    .orderId(idOrder)
                    .orderState(OrderState.COMPLETED)
                    .originalAmount(cryptoQty)
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

    public void awaitLatch(CountDownLatch latch) {
        try {
            latch.await(); // Блокируем текущий поток до завершения задачи
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Восстановление статуса прерывания
            log.error("Торговый поток был прерван:", e);
        }
    }

    private void cleanUp() {
        lastPrice = null;
        endPrice = null;
        buyLevels.clear();
        basicChange = null;
        producerServiceExchange = null;
        webSocketCommand = null;
        disposable = null;
    }
}
