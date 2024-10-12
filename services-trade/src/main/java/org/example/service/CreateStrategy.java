package org.example.service;

import org.example.entity.NodeUser;
import org.example.strategy.Strategy;
import org.example.xchange.BasicChangeInterface;

public interface CreateStrategy {
    Strategy createStrategy(NodeUser nodeUser, BasicChangeInterface basicChange);
}
