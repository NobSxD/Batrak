package org.example.strategy.impl;

import com.github.benmanes.caffeine.cache.Cache;
import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.OrderState;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.Strategy;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public abstract class StrategyBasic implements Strategy {
    protected CountDownLatch latch = new CountDownLatch(1);
    protected BasicChangeInterface basicChange;

    public abstract void trade(NodeUser nodeUser, ProcessServiceCommand producerServiceExchange,
                               WebSocketCommand webSocketCommand, NodeUserDAO nodeUserDAO, NodeOrdersDAO ordersDAO,
                               Cache<Long, NodeUser> nodeUserCache, BasicChangeInterface basicChange);

    public abstract NodeOrder bay(OrderBook orderBook, NodeUser nodeUser);

    public abstract NodeOrder sel(OrderBook orderBook, NodeUser nodeUser, NodeOrder nodeOrder);

    public abstract NodeOrder processOrder(NodeUser nodeUser,
                                           Instrument currencyPair, List<BigDecimal> priceAndAmount, Order.OrderType orderType,
                                           boolean isBid) throws InterruptedException;

    public void cancelOrder(NodeOrder nodeOrder) {
        if (basicChange == null){
            //TODO придумать логику отмены
            return;
        }
        basicChange.cancelOrder(nodeOrder.getInstrument(), nodeOrder.getOrderId());
        nodeOrder.setOrderState(OrderState.PENDING_CANCEL);
        latch.countDown();
    }

}
