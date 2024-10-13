package org.example.strategy.impl;

import io.reactivex.rxjava3.schedulers.Schedulers;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public final Set<BigDecimal> buyLevels = new HashSet<>();
    BigDecimal endPrice = new BigDecimal("999999999999");
    BigDecimal buyPercent;
    BigDecimal sellPercent;
    BigDecimal currentPrice;
    BigDecimal lastPrice;

    private final Instrument instrument;
    private final BigDecimal coinAmount;
    private final boolean demoTrade;
    private final int scale;
    private final NodeUserDAO nodeUserDAO;
    private final NodeOrdersDAO nodeOrdersDAO;
    private NodeUser nodeUser;

    public GridTrading(NodeUser nodeUser, BasicChangeInterface basicChange, NodeUserDAO nodeUserDAO,
                       WebSocketCommand webSocketCommand, ProcessServiceCommand producerServiceExchange,
                       NodeOrdersDAO nodeOrdersDAO) {
        super(new TradeStatusManager());
        this.nodeUser = nodeUser;
        this.basicChange = basicChange;
        this.nodeUserDAO = nodeUserDAO;
        this.webSocketCommand = webSocketCommand;
        this.producerServiceExchange = producerServiceExchange;
        this.nodeOrdersDAO = nodeOrdersDAO;
        this.instrument = new CurrencyPair(nodeUser.getConfigTrade().getNamePair());
        this.coinAmount = nodeUser.getConfigTrade().getAmountOrder();
        this.demoTrade = nodeUser.getConfigTrade().isEnableDemoTrading();
        this.scale = nodeUser.getConfigTrade().getScale();
        this.buyPercent = BigDecimal.valueOf(nodeUser.getConfigTrade().getStepBayD());
        this.sellPercent = BigDecimal.valueOf(nodeUser.getConfigTrade().getStepSellD());
    }


    @Override
    public TradeStatusManager getTradeStatusManager() {
        return tradeStatusManager;
    }

    @Override
    public void tradeStart() {
        Runnable task = () -> {
            try {
                TradeState state = tradeStatusManager.getCurrentTradeState();
                switch (state) {
                    case TRADE_STOP, TRADE_CANCEL -> {
                        scheduler.shutdown();
                        resultTrade(nodeOrdersDAO, nodeUser);
                        return;
                    }
                }
                startTradingCycle(nodeUser);
            }catch (Exception e) {
                log.error("Ошибка в торговом цикле: ", e);
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 40, TimeUnit.SECONDS);

    }


    public void startTradingCycle(NodeUser nodeUser) {
        WebSocketChange webSocketChange = webSocketCommand.webSocketChange(nodeUser);

        disposable = webSocketChange.getCurrencyRateStream()
                .observeOn(Schedulers.io())
                .takeUntil(rate -> rate.getLimitPrice().compareTo(endPrice) >= 0)
                .subscribe(rate -> {
                    BigDecimal currentPrice = rate.getLimitPrice();

                    if (shouldBuy(currentPrice)) {
                        tradeStatusManager.buy();
                        executeBuyOrder(currentPrice, nodeUser);
                    } else if (shouldSell(currentPrice)) {
                        tradeStatusManager.sell();
                        executeSellOrder(currentPrice, nodeUser);
                    }
                }, error -> {
                    log.error("Error occurred: {}", error.getMessage());
                }, () -> {
                    log.info("Reached endPrice, stopping the trading cycle");
                });

    }

    public boolean shouldBuy(BigDecimal currentPrice) {
        if (lastPrice == null) {
            endPrice = currentPrice;
            // Первая сделка, покупаем независимо от уровня
            return true;
        }
        BigDecimal targetBuyPrice = lastPrice.subtract(lastPrice.multiply(buyPercent));
        // Проверяем, можно ли купить на этом уровне
        return currentPrice.compareTo(targetBuyPrice) <= 0 && !buyLevels.contains(currentPrice);
    }

    public boolean shouldSell(BigDecimal currentPrice) {
        BigDecimal targetSellPrice = lastPrice.add(lastPrice.multiply(sellPercent));
        // Проверяем, можно ли продать на этом уровне
        boolean canSell = currentPrice.compareTo(targetSellPrice) >= 0;
        boolean contains = buyLevels.contains(lastPrice);
        return canSell && contains;
    }

    public void executeBuyOrder(BigDecimal price, NodeUser nodeUser) {
        this.currentPrice = price;
        lastPrice = price;
        buyLevels.add(price);
        process(Order.OrderType.BID, nodeUser);
    }

    protected void executeSellOrder(BigDecimal price, NodeUser nodeUser) {
        this.currentPrice = price;
        buyLevels.remove(lastPrice);
        lastPrice = price;
        process(Order.OrderType.ASK, nodeUser);
    }

    public void process(Order.OrderType orderType, NodeUser nodeUser) {
        try {
            BigDecimal amount = coinAmount;
            if (orderType.equals(Order.OrderType.ASK)){
                amount = FinancialCalculator.increaseByPercentage(coinAmount, sellPercent);
            }
            BigDecimal cryptoQty = CurrencyConverter.convertCurrency(currentPrice, amount, scale);
            if (cryptoQty == null) {
                throw new IllegalArgumentException("Converted cryptocurrency amount is null");
            }

            MarketOrder marketOrder = basicChange.createMarketOrder(orderType, cryptoQty, instrument);
            if (marketOrder == null) {
                throw new IllegalStateException("Failed to create market order");
            }

            String idOrder = basicChange.placeMarketOrder(marketOrder, demoTrade);

            BigDecimal usdAmount = CurrencyConverter.convertUsdt(currentPrice, amount);
            if (usdAmount == null) {
                throw new IllegalArgumentException("Converted USD amount is null");
            }

            NodeOrder nodeOrder = NodeOrder.builder()
                    .limitPrice(currentPrice)
                    .type(orderType.name())
                    .orderId(idOrder)
                    .orderState(OrderState.COMPLETED)
                    .originalAmount(cryptoQty)
                    .usd(usdAmount)
                    .timestamp(new Date())
                    .instrument(nodeUser.getConfigTrade().getNamePair())
                    .checkReal(nodeUser.getConfigTrade().isEnableDemoTrading())
                    .nodeUser(nodeUser)
                    .menuStrategy(nodeUser.getConfigTrade().getStrategy())
                    .build();
            finalizeOrder(nodeOrder);

            if (nodeUser.getOrders() == null) {
                nodeUser.setOrders(new ArrayList<>());
            }
            nodeUser.getOrders().add(nodeOrder);
            nodeUserDAO.save(nodeUser);
        } catch (FundsExceededException e) {
            handleFundsExceededException(nodeUser, e);
        } catch (IllegalArgumentException e) {
            handleIllegalArgumentException(nodeUser, e);
        } catch (IllegalStateException e) {
            handleIllegalStateException(nodeUser, e);
        } catch (Exception e) {
            handleGeneralException(nodeUser, e);
        }
    }
}
