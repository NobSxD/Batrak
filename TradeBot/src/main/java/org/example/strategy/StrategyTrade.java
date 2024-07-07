package org.example.strategy;

import org.example.entity.NodeUser;
import org.example.entity.enams.StrategyEnams;
import org.example.xchange.BasicChangeInterface;
import org.springframework.stereotype.Component;

@Component
public interface StrategyTrade {
	String trade(NodeUser nodeUser, BasicChangeInterface basicChange);
	StrategyEnams getType();
}
