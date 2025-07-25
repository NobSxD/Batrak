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
import org.example.xchange.DTO.WalletDTO;
import org.example.xchange.finance.CurrencyConverter;
import org.example.xchange.finance.FinancialCalculator;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.FundsExceededException;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.OrderState;
import org.example.entity.enams.state.TradeState;

import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;

import static java.lang.Thread.sleep;

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
        CurrencyPair currencyPair = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
        marketTradeDetails = new MarketTradeDetails(
                currencyPair,
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
        this.walletDTO = new WalletDTO(nodeUser.getConfigTrade().getDeposit(), currencyPair);
        tradeStatusManager.startTrading();
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
                if (tradeStatusManager.isStop()) {
                    log.info("Поступила команда на остановку трейдинга");
                    break;
                }

                startTradingCycle(nodeUser);
                log.info("Блокирую поток пока метод startTradingCycle не разблокирует его");
                awaitLatch();

                if (tradeStatusManager.isCancel()) {
                    log.info("Поступила команда на отмену, поток разблокирован, торговля отменена");
                    break;
                }
                // Подождите 40 секунд перед следующим циклом
                log.info("Цикл завершился, ожидаем 40 секунда и начинаем новую торговлю");
                log.info("tradeStatusManager: {}, lastPrice : {}, endPrice: {}, user: {}",
                        tradeStatusManager.getCurrentTradeState(), marketTradeDetails.getLastPrice(),
                        marketTradeDetails.getEndPrice(), nodeUser.getUsername());
                marketTradeDetails.setLastPrice(null);
                marketTradeDetails.setEndPrice(null);
                sleep(40000);

            }
        } catch (Exception e) {
            producerServiceExchange.sendAnswer("Ошибка: " + e.getMessage(), nodeUser.getChatId());
            log.error("Ошибка в торговом цикле: ", e);
        } finally {
            tradeStatusManager.stopOK();
            resultTrade(walletDTO, nodeUser, currentPrice());
            cleanUp();
        }
    }

    @Override
    public BigDecimal currentPrice() {
        return CurrencyConverter.validScale(currentPrice);
    }


    @Transactional
    public void startTradingCycle(NodeUser nodeUser) {
        WebSocketChange webSocketChange = webSocketCommand.webSocketChange(nodeUser);
        log.info("webSocketChange {} ", webSocketChange.getClass().getName());
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
                    tradeStatusManager.stopOK();
                    producerServiceExchange.sendAnswer(error.getMessage(), nodeUser.getChatId());
                });
    }


    public NodeOrder processPrice(BigDecimal currentPrice, NodeUser nodeUser) {
        NodeOrder nodeOrder = null;
        if (tradeStatusManager.getCurrentTradeState().equals(TradeState.TRADE_START)) {
            log.info("-------------------------------------------------------");
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
            log.info("-------------------------------------------------------");
            nodeOrder = executeSellOrder(currentPrice, nodeUser);
            marketTradeDetails.setRecentAction("LAST_SELL");
            tradeStatusManager.sellLast();
            tradeStatusManager.startTrading();
            tradeStatusManager.countDown();
            dispose(disposable);
            log.info("2.LAST_SELL: sellPrice: {}, nodeOrderPrice: {} ", currentPrice, nodeOrder.getLimitPrice());
        } else if (shouldBuy(currentPrice)) {
            log.info("-------------------------------------------------------");
            nodeOrder = executeBuyOrder(currentPrice, nodeUser);
            marketTradeDetails.setNextBay(FinancialCalculator.subtractPercentage(currentPrice, marketTradeDetails.getStepBay()));
            marketTradeDetails.setNexSell(FinancialCalculator.addPercentage(marketTradeDetails.getLastPrice(), marketTradeDetails.getStepSell()));
            marketTradeDetails.setRecentAction("BAY");
            log.info("3.BAY: bayPrice: {}, nextBay: {}, nextSell: {}, nodeOrderPrice: {} ",
                    currentPrice, marketTradeDetails.getNextBay(), marketTradeDetails.getNexSell(), nodeOrder.getLimitPrice());
        } else if (shouldSell(currentPrice)) {
            log.info("-------------------------------------------------------");
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
        boolean count = marketTradeDetails.getCountDeal() < marketTradeDetails.getMaxCountDeal();
        boolean canBuy = currentPrice.compareTo(marketTradeDetails.getNextBay()) <= 0;
        if (!count) {
            tradeStatusManager.pendingTrading();
            return false;
        }
        if (canBuy) {
            log.info("shouldBuy: currentPrice - nextBay = {} <= {}, lastPrice = {}, getCountDeal {}, getMaxCountDeal {}",
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
            BigDecimal coin = CurrencyConverter.convertCurrency(marketTradeDetails.getLastPrice(), amount, marketTradeDetails.getScale());
            walletDTO.set(amount, coin, orderType);
            log.info("Тип ордера {}, сумма {}", orderType, amount);

            List<BigDecimal> priceAndAmount = List.of(marketTradeDetails.getLastPrice(), amount);
            LimitOrder order = basicChange.createOrder(marketTradeDetails.getInstrument(), priceAndAmount, orderType, marketTradeDetails.getScale());

            String idOrder = basicChange.placeLimitOrder(order);
            walletDTO.addCountDelay();

            return NodeOrder.builder()
                    .limitPrice(marketTradeDetails.getLastPrice())
                    .type(orderType.name())
                    .orderId(idOrder)
                    .orderState(OrderState.COMPLETED)
                    .originalAmount(coin)
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
        } catch (Exception e) {
            handleGeneralException(nodeUser, e);
        }
        return null;
    }

    public void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void awaitLatch() {
        try {
            tradeStatusManager.newCountDownLatch(1);
            tradeStatusManager.getCountDownLatch().await(); // Блокируем текущий поток до завершения задачи
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
