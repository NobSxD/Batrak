package org.example.strategy;

import org.example.entity.NodeUser;
import org.example.xchange.BasicChangeInterface;
import org.springframework.stereotype.Component;

@Component
public interface StrategyCommand {
	String trade(NodeUser nodeUser, BasicChangeInterface basicChange);
}
