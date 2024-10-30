package org.example.strategy.impl;

import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.OrderState;
import org.example.entity.enams.state.TradeState;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    private final boolean demoTrade;
    private final double stepSell;
    private final double stepBay;
    private final int scale;
    BigDecimal endPrice;
    BigDecimal currentPrice;
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
        this.demoTrade = nodeUser.getConfigTrade().isEnableDemoTrading();
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
                    break;
                }

                startTradingCycle(nodeUser);
                awaitLatch(tradeStatusManager.getCountDownLatch());

                if (tradeStatusManager.getCurrentTradeState() == TradeState.TRADE_CANCEL) {
                    resultTrade(nodeUser);
                    break;
                }
                // Подождите 40 секунд перед следующим циклом
                try {
                    log.info("Достигнута конечная цена, завершаем торговый цикл. : {}", currentPrice);
                    tradeStatusManager.sellLast();
                    lastPrice = null;
                    endPrice = null;
                    Thread.sleep(40000);
                } catch (InterruptedException e) {
                    log.error("Поток был прерван: ", e);
                    Thread.currentThread().interrupt(); // Восстановление прерывания
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Ошибка в торговом цикле: ", e);
        }
    }


    @Transactional
    public void startTradingCycle(NodeUser nodeUser) {
        WebSocketChange webSocketChange = webSocketCommand.webSocketChange(nodeUser);

        // Подписываемся на изменения
        disposable = webSocketChange.exchangePair(instrument)
                .observeOn(Schedulers.io())
                .buffer(10, TimeUnit.SECONDS)  // Оптимизация: собираем данные за промежуток времени, чтобы не сохранять каждое изменение
                .subscribe(rateList -> {
                    List<NodeOrder> newOrders = new ArrayList<>();
                    for (NodeOrder rate : rateList) {
                        BigDecimal currentPrice = rate.getLimitPrice();
                        NodeOrder nodeOrder = processPrice(currentPrice, nodeUser);
                        if (nodeOrder != null) {
                            newOrders.add(nodeOrder);
                        }
                    }

                    // Добавляем заказы только если есть новые
                    if (!newOrders.isEmpty()) {
                        if (nodeUser.getOrders() == null) {
                            nodeUser.setOrders(new ArrayList<>());
                        }
                        nodeUser.getOrders().addAll(newOrders);
                        nodeUserDAO.save(nodeUser);  // Сохраняем изменения только один раз
                    }
                }, error -> {
                    log.error("Error occurred: {}", error.getMessage());
                });
    }


    public NodeOrder processPrice(BigDecimal currentPrice, NodeUser nodeUser) {
        NodeOrder nodeOrder = null;
        TradeState state = tradeStatusManager.getCurrentTradeState();
        if (state.equals(TradeState.TRADE_START)) {
            log.info("Прайс на первую покупку : {}", currentPrice);
            endPrice = FinancialCalculator.increaseByPercentage(currentPrice, BigDecimal.valueOf(stepSell));
            nodeOrder = executeBuyOrder(currentPrice, nodeUser);
            tradeStatusManager.runTrading();
        }
         if (shouldBuy(currentPrice)) {
            log.info("Прайс на покупку : {}", currentPrice);
            nodeOrder = executeBuyOrder(currentPrice, nodeUser);
        } else if (shouldSell(currentPrice)) {
            log.info("Прайс на продажу : {}", currentPrice);
            nodeOrder = executeSellOrder(currentPrice, nodeUser);
        }else if (shouldLastSell(currentPrice)) {
             log.info("Прайс на последнию продажу : {}", currentPrice);
             nodeOrder = executeSellOrder(currentPrice, nodeUser);
         }
        return nodeOrder;
    }

    public void awaitLatch(CountDownLatch latch) {
        try {
            latch.await(); // Блокируем текущий поток до завершения задачи
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Восстановление статуса прерывания
            log.error("Торговый поток был прерван:", e);
        }
    }


    public boolean shouldBuy(BigDecimal currentPrice) {
        BigDecimal targetBuyPrice = lastPrice.subtract(lastPrice.multiply(BigDecimal.valueOf(stepBay)));
        // Проверяем, можно ли купить на этом уровне
        boolean canBuy = currentPrice.compareTo(targetBuyPrice) <= 0;
        boolean contains = !buyLevels.contains(currentPrice);
        if (canBuy && contains){
            log.debug("BAY: currentPrice = {}, targetBuyPrice = {}, buyLevels = {} ",currentPrice,targetBuyPrice, buyLevels );
        }
        return canBuy && contains;
    }

    public boolean shouldSell(BigDecimal currentPrice) {
        BigDecimal targetSellPrice = lastPrice.add(lastPrice.multiply(BigDecimal.valueOf(stepSell)));
        // Проверяем, можно ли продать на этом уровне
        boolean canSell = currentPrice.compareTo(targetSellPrice) >= 0;
        boolean contains = buyLevels.contains(lastPrice);
        if (canSell && contains){
            log.debug("SELL: currentPrice = {}, targetBuyPrice = {}, buyLevels = {} ",currentPrice,targetSellPrice, buyLevels );
        }
        return canSell && contains;
    }
    public boolean shouldLastSell(BigDecimal currentPrice) {

        boolean canSell = endPrice.compareTo(currentPrice) <= 0;
        boolean contains = buyLevels.contains(lastPrice);
        if (canSell && contains){
            log.debug("SELL LAST: currentPrice = {}, endPrice = {}, buyLevels = {} ",currentPrice,endPrice, buyLevels );
        }
        return canSell && contains;
    }

    public NodeOrder executeBuyOrder(BigDecimal price, NodeUser nodeUser) {
        this.currentPrice = price;
        lastPrice = price;
        buyLevels.add(price);
        return process(Order.OrderType.BID, nodeUser);
    }

    public NodeOrder executeSellOrder(BigDecimal price, NodeUser nodeUser) {
        this.currentPrice = price;
        buyLevels.remove(lastPrice);
        lastPrice = price;
        return process(Order.OrderType.ASK, nodeUser);
    }

    public NodeOrder process(Order.OrderType orderType, NodeUser nodeUser) {
        try {
            BigDecimal amount = coinAmount;
            if (orderType.equals(Order.OrderType.ASK)) {
                amount = FinancialCalculator.increaseByPercentage(coinAmount, BigDecimal.valueOf(stepSell));
            }
            BigDecimal cryptoQty = CurrencyConverter.convertCurrency(currentPrice, amount, scale);

            MarketOrder marketOrder = basicChange.createMarketOrder(orderType, cryptoQty, instrument);
            if (marketOrder == null) {
                throw new IllegalStateException("Failed to create market order");
            }

            String idOrder = basicChange.placeMarketOrder(marketOrder, demoTrade);

            NodeOrder nodeOrder = NodeOrder.builder()
                    .limitPrice(currentPrice)
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
            finalizeOrder(nodeOrder);
            return nodeOrder;
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
}
