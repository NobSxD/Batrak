package org.example.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.OrderState;
import org.example.strategy.impl.helper.TradeStatusManager;
import org.example.websocet.WebSocketChange;
import org.example.xchange.finance.CurrencyConverter;
import org.example.xchange.finance.FinancialCalculator;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
public class GridTrading extends StrategyBasic {
    public GridTrading(Instrument instrument, BigDecimal coinAmount, boolean demoTread, int scale) {
        super(new TradeStatusManager());
        this.instrument = instrument;
        this.coinAmount = coinAmount;
        this.demoTrade = demoTread;
        this.scale = scale;
    }

    BigDecimal endPrice; //последняя цена перед завершением цыкла
    List<BigDecimal> gridLevels; //сетка ордеров на покупку
    BigDecimal deposit = new BigDecimal("100"); //сумма банка
    BigDecimal step = new BigDecimal("1");

    private final Instrument instrument;
    private final BigDecimal coinAmount;
    private final boolean demoTrade;
    private final int scale;


    @Override
    public TradeStatusManager getTradeStatusManager() {
        return null;
    }


    @Override
    public NodeOrder bay(NodeUser nodeUser) {
        Order.OrderType orderType = Order.OrderType.BID;
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
        int size = FinancialCalculator.sizeGridLevels(deposit, coinAmount);
        gridLevels = FinancialCalculator.gridLevels(endPrice, step, size, nodeUser.getConfigTrade().getScale());
        return nodeOrder;
    }


    @Override
    public NodeOrder sel(NodeUser nodeUser) {
        Order.OrderType orderType = Order.OrderType.ASK;
        BigDecimal price = FinancialCalculator.stepPrice(endPrice, step);
        BigDecimal amount = CurrencyConverter.convertCurrency(price, coinAmount, scale);
        List<BigDecimal> priceAndAmount = List.of(price, amount);
        LimitOrder limitOrder = basicChange.createOrder(instrument, priceAndAmount, orderType);
        String idOrder = basicChange.placeLimitOrder(limitOrder, demoTrade);



        NodeOrder nodeOrder = NodeOrder.builder()
                .type(orderType.name())
                .orderId(idOrder)
                .orderState(OrderState.FILLED)
                .originalAmount(coinAmount)
                .usd(CurrencyConverter.convertUsdt(price, coinAmount))
                .timestamp(new Date())
                .instrument(instrument.toString())
                .checkReal(demoTrade)
                .nodeUser(nodeUser)
                .menuStrategy(nodeUser.getConfigTrade().getStrategy())
                .build();
        return nodeOrder;
    }

    @Override
    public NodeOrder processOrder(NodeUser nodeUser, Instrument currencyPair, List<BigDecimal> priceAndAmount,
                                  Order.OrderType orderType, boolean isBid) throws InterruptedException {
        return null;
    }
}
