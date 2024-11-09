package org.example.strategy.impl;

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
                    log.info("Достигнута конечная цена, завершаем торговый цикл. : {}", marketTradeDetails.getLastPrice());
                    tradeStatusManager.sellLast();
                    marketTradeDetails.setLastPrice(null);
                    marketTradeDetails.setEndPrice(null);
                    log.info("""
                                    Цикл завершился, ожидаем 40 секунда и начинаем новую торговлю,
                                    tradeStatusManager: {}
                                    lastPrice : {}
                                    endPrice: {}
                                    buyLevels : {}""",
                            tradeStatusManager.getCurrentTradeState(), marketTradeDetails.getLastPrice(),
                            marketTradeDetails.getEndPrice(), marketTradeDetails.getBuyLevels());
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
        }
    }


    @Transactional
    public void startTradingCycle(NodeUser nodeUser) {
        WebSocketChange webSocketChange = webSocketCommand.webSocketChange(nodeUser);
        disposable = webSocketChange.exchangePair(marketTradeDetails.getInstrument())
                .observeOn(Schedulers.io())
                .subscribe(rate -> {
                    NodeOrder nodeOrder = processPrice(rate, nodeUser);
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
        if (tradeStatusManager.getCurrentTradeState().equals(TradeState.TRADE_START)) {
            nodeOrder = executeBuyOrder(currentPrice, nodeUser);
            marketTradeDetails.setEndPrice(FinancialCalculator.increaseByPercentage(currentPrice, marketTradeDetails.getStepSell()));
            marketTradeDetails.setNextBay(FinancialCalculator.subtractPercentage(currentPrice, marketTradeDetails.getStepBay()));
            marketTradeDetails.setNexSell(FinancialCalculator.addPercentage(marketTradeDetails.getLastPrice(), marketTradeDetails.getStepSell()));
            tradeStatusManager.runTrading();
            log.info("1.FIRST_BAY: bayPrice {}, endPrice: {}, nextBay: {}, nextSell: {}, nodeOrderPrice: {} ",
                    currentPrice, marketTradeDetails.getEndPrice(), marketTradeDetails.getNextBay(),
                    marketTradeDetails.getNexSell(), nodeOrder.getLimitPrice());
        }
        if (shouldBuy(currentPrice)) {
            nodeOrder = executeBuyOrder(currentPrice, nodeUser);
            marketTradeDetails.setNextBay(FinancialCalculator.subtractPercentage(currentPrice, marketTradeDetails.getStepBay()));
            marketTradeDetails.setNexSell(FinancialCalculator.addPercentage(marketTradeDetails.getLastPrice(), marketTradeDetails.getStepSell()));
            log.info("2.BAY: bayPrice: {}, nextBay: {}, nextSell: {}, nodeOrderPrice: {} ",
                    currentPrice, marketTradeDetails.getNextBay(), marketTradeDetails.getNexSell(), nodeOrder.getLimitPrice());
        } else if (shouldSell(currentPrice)) {
            nodeOrder = executeSellOrder(currentPrice, nodeUser);
            marketTradeDetails.setNextBay(FinancialCalculator.subtractPercentage(currentPrice, marketTradeDetails.getStepBay()));
            marketTradeDetails.setNexSell(FinancialCalculator.addPercentage(marketTradeDetails.getLastPrice(), marketTradeDetails.getStepSell()));
            log.info("3.SELL: sellPrice {} nextBay: {}, nextSell: {}, nodeOrderPrice: {} ",
                    currentPrice, marketTradeDetails.getNextBay(), marketTradeDetails.getNexSell(), nodeOrder.getLimitPrice());
        } else if (shouldLastSell(currentPrice)) {
            nodeOrder = executeSellOrder(currentPrice, nodeUser);
            tradeStatusManager.getCountDownLatch().countDown();
            log.info("4.LAST_SELL: sellPrice: {}, nodeOrderPrice: {} ", currentPrice, nodeOrder.getLimitPrice());
        }
        return nodeOrder;
    }

    public boolean shouldBuy(BigDecimal currentPrice) {
        boolean canBuy = currentPrice.compareTo(marketTradeDetails.getNextBay()) <= 0;
        boolean contains = !marketTradeDetails.getBuyLevels().contains(currentPrice);
        if (canBuy && contains) {
            log.info("shouldSell: currentPrice - nextBay = {} <= {}, buyLevels = {}, lastPrice = {} ",
                    currentPrice, marketTradeDetails.getNextBay(), marketTradeDetails.getBuyLevels(), marketTradeDetails.getLastPrice());
        }
        return canBuy && contains;
    }

    public boolean shouldSell(BigDecimal currentPrice) {
        boolean canSell = currentPrice.compareTo(marketTradeDetails.getNexSell()) >= 0;
        boolean contains = !marketTradeDetails.getBuyLevels().contains(marketTradeDetails.getLastPrice());
        if (canSell && contains) {
            log.info("shouldSell: currentPrice - nexSell = {} >= {}, buyLevels = {}, lastPrice = {} ",
                    currentPrice, marketTradeDetails.getNexSell(), marketTradeDetails.getBuyLevels(), marketTradeDetails.getLastPrice());
        }
        return canSell && contains;
    }

    public boolean shouldLastSell(BigDecimal currentPrice) {
        boolean canSell = marketTradeDetails.getEndPrice().compareTo(currentPrice) <= 0;
        boolean contains = marketTradeDetails.getBuyLevels().contains(marketTradeDetails.getLastPrice());
        if (canSell && contains) {
            log.info("SELL LAST:  endPrice - currentPrice = {} <= {}, buyLevels = {} ", marketTradeDetails.getEndPrice(),
                    currentPrice, marketTradeDetails.getBuyLevels());
        }
        return canSell && contains;
    }

    public NodeOrder executeBuyOrder(BigDecimal price, NodeUser nodeUser) {
        marketTradeDetails.setLastPrice(price);
        marketTradeDetails.getBuyLevels().add(price);
        return process(Order.OrderType.BID, nodeUser);
    }

    public NodeOrder executeSellOrder(BigDecimal price, NodeUser nodeUser) {
        marketTradeDetails.getBuyLevels().remove(marketTradeDetails.getLastPrice());
        marketTradeDetails.setLastPrice(price);
        return process(Order.OrderType.ASK, nodeUser);
    }

    public NodeOrder process(Order.OrderType orderType, NodeUser nodeUser) {
        try {
            BigDecimal amount = marketTradeDetails.getCoinAmount();
            if (orderType.equals(Order.OrderType.ASK)) {
                amount = FinancialCalculator.increaseByPercentage(marketTradeDetails.getCoinAmount(), marketTradeDetails.getStepSell());
            }
            BigDecimal cryptoQty = CurrencyConverter.convertCurrency(marketTradeDetails.getLastPrice(), amount, marketTradeDetails.getScale());

            MarketOrder marketOrder = basicChange.createMarketOrder(orderType, cryptoQty, marketTradeDetails.getInstrument());

            String idOrder = basicChange.placeMarketOrder(marketOrder);

            return NodeOrder.builder()
                    .limitPrice(marketTradeDetails.getLastPrice())
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
