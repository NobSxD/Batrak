package org.example.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.StrategyEnams;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.StrategyTrade;
import org.example.xchange.BasicChangeInterface;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionsOfEvents implements StrategyTrade {
	
	private BasicChangeInterface basicChange;
	private final ProcessServiceCommand producerServiceExchange;
	private final NodeUserDAO nodeUserDAO;
	private final NodeOrdersDAO ordersDAO;
	
	@Override
	public String trade(NodeUser nodeUser, BasicChangeInterface basicChange) {
		
		return null;
	}
	
	@Override
	public StrategyEnams getType() {
		return null;
	}
}
