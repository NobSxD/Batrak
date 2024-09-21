package org.example.strategy;

import com.github.benmanes.caffeine.cache.Cache;
import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.impl.helper.TradeStatusManager;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;

public interface Strategy {
    void trade(NodeUser nodeUser, ProcessServiceCommand producerServiceExchange, WebSocketCommand webSocketCommand,
               NodeUserDAO nodeUserDAO, NodeOrdersDAO ordersDAO, Cache<Long, NodeUser> nodeUserCache, BasicChangeInterface basicChange) ;
    void cancelOrder(NodeOrder nodeOrder);
    TradeStatusManager getTradeStatusManager();
}
