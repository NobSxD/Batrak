package org.example.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.dao.StatisticsTradeDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.StrategyEnams;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.StrategyTrade;
import org.example.xchange.BasicChangeInterface;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PredictionsOfEvents implements StrategyTrade {

	private BasicChangeInterface basicChange;
	private final ProcessServiceCommand producerServiceExchange;
	private final NodeUserDAO nodeUserDAO;
	private final NodeOrdersDAO ordersDAO;
	private final StatisticsTradeDAO statisticsTradeDAO;

	private static final Logger logger = LoggerFactory.getLogger(PredictionsOfEvents.class);
	@Override
	public String trade(NodeUser nodeUser, BasicChangeInterface basicChange) {


		return null;
	}

	@Override
	public StrategyEnams getType() {
		return null;
	}
}
