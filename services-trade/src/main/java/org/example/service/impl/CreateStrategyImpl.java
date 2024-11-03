package org.example.service.impl;

import org.example.service.CreateStrategy;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.Strategy;
import org.example.strategy.impl.GridTrading;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;

import lombok.RequiredArgsConstructor;

import org.example.entity.NodeUser;

import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateStrategyImpl implements CreateStrategy {
    private final NodeUserDAO nodeUserDAO;
    private final NodeOrdersDAO nodeOrdersDAO;
    private final WebSocketCommand webSocketCommand;
    private final ProcessServiceCommand processServiceCommand;

    public Strategy createStrategy(NodeUser nodeUser, BasicChangeInterface basicChange) {
        Strategy strategy = null;
        switch (nodeUser.getConfigTrade().getStrategy()) {
            case GridTrading -> strategy = new GridTrading(nodeUser, basicChange, nodeUserDAO, webSocketCommand,
                    processServiceCommand, nodeOrdersDAO);
        }
        return strategy;
    }
}
