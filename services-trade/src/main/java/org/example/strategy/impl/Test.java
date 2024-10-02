package org.example.strategy.impl;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.OrderState;
import org.example.strategy.impl.helper.TradeStatusManager;
import org.example.websocet.WebSocketChange;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.finance.CurrencyConverter;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class Test extends StrategyBasic{

    BigDecimal endPrice; // Target price to end the cycle
    List<BigDecimal> gridLevels; // Grid levels for orders (if using a grid strategy)
    BigDecimal deposit = new BigDecimal("100"); // Total available funds
    BigDecimal lastBuyPrice; // Price at which the last buy was executed
    BigDecimal nPercent = new BigDecimal("0.05"); // 5% price change threshold
    Instrument instrument; // The trading pair, e.g., BTC/USDT
    int scale = 8; // Decimal scale for price calculations
    boolean demoTrade; // Flag for demo trading
    private final Set<BigDecimal> buyLevels = new HashSet<>();
    protected BasicChangeInterface basicChange;
    protected WebSocketCommand webSocketCommand;
    private final BigDecimal coinAmount;

    public Test(BigDecimal coinAmount) {
        super(new TradeStatusManager());
        this.coinAmount = coinAmount;
    }


    public void startTradingCycle(NodeUser nodeUser) {
        WebSocketChange webSocketChange = webSocketCommand.webSocketChange(nodeUser);

        Disposable disposable = webSocketChange.getCurrencyRateStream()
                .observeOn(Schedulers.io())
                .takeUntil(rate -> rate.getLimitPrice().compareTo(endPrice) >= 0)
                .subscribe(rate -> {BigDecimal currentPrice = rate.getLimitPrice();

                    if (shouldBuy(currentPrice)) {
                        executeBuyOrder(currentPrice, nodeUser);
                    } else if (shouldSell(currentPrice)) {
                        executeSellOrder(currentPrice, nodeUser);
                    }
                }, error -> {
                    log.error("Error occurred: {}", error.getMessage());
                }, () -> {
                    log.info("Reached endPrice, stopping the trading cycle");
                });
        disposable.dispose();
    }

    private boolean shouldBuy(BigDecimal currentPrice) {
        BigDecimal targetBuyPrice = lastBuyPrice.subtract(lastBuyPrice.multiply(nPercent));
        // Проверяем, можно ли купить на этом уровне
        return currentPrice.compareTo(targetBuyPrice) <= 0 && !buyLevels.contains(currentPrice);
    }

    private boolean shouldSell(BigDecimal currentPrice) {
        BigDecimal targetSellPrice = lastBuyPrice.add(lastBuyPrice.multiply(nPercent));
        // Проверяем, можно ли продать на этом уровне
        return currentPrice.compareTo(targetSellPrice) >= 0 && buyLevels.contains(targetSellPrice.subtract(lastBuyPrice.multiply(nPercent)));
    }

    private void executeBuyOrder(BigDecimal price, NodeUser nodeUser) {
        lastBuyPrice = price;
        BigDecimal level = price;
        buyLevels.add(level);
        process(Order.OrderType.BID, nodeUser);
    }

    private void executeSellOrder(BigDecimal price, NodeUser nodeUser) {
        BigDecimal level = price.subtract(lastBuyPrice.multiply(nPercent));
        buyLevels.remove(level);
        process(Order.OrderType.ASK, nodeUser);
    }
    private NodeOrder process(Order.OrderType orderType, NodeUser nodeUser){
        MarketOrder marketOrder = basicChange.createMarketOrder(orderType, coinAmount, instrument);
        String idOrder = basicChange.placeMarketOrder(marketOrder, demoTrade);
        WebSocketChange webSocketChange = webSocketCommand.webSocketChange(nodeUser);

        webSocketChange.getCurrencyRateStream()
                .firstElement()
                .subscribe(rate -> endPrice = rate.getLimitPrice(),

                        error -> log.error("Произошла ошибка: {}", error.getMessage())
                );
        tradeStatusManager.sell();
        NodeOrder nodeOrder = NodeOrder.builder()
                .type(orderType.name())
                .orderId(idOrder)
                .orderState(OrderState.FILLED)
                .originalAmount(endPrice)
                .usd(CurrencyConverter.convertUsdt(endPrice, coinAmount))
                .timestamp(new Date())
                .instrument(nodeUser.getConfigTrade().getNamePair())
                .checkReal(nodeUser.getConfigTrade().isEnableDemoTrading())
                .nodeUser(nodeUser)
                .menuStrategy(nodeUser.getConfigTrade().getStrategy())
                .build();
        return nodeOrder;
    }

    @Override
    public TradeStatusManager getTradeStatusManager() {
        return null;
    }

    @Override
    public NodeOrder bay(NodeUser nodeUser) {
        return null;
    }

    @Override
    public NodeOrder sel(NodeUser nodeUser) {
        return null;
    }
}
