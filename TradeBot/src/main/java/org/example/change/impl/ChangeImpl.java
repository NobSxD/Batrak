package org.example.change.impl;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.change.Change;
import org.example.change.ChangeFactory;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.TradeState;
import org.example.strategy.StrategyTrade;
import org.example.xchange.BasicChangeInterface;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class ChangeImpl implements Change {
	private final Logger logger = Logger.getLogger(ChangeImpl.class);
	private final StrategyTrade strategyTrade;
	private final NodeUserDAO nodeUserDAO;

	@Override
	public void tradeStart(NodeUser nodeUser) {
		BasicChangeInterface change = ChangeFactory.createChange(nodeUser);

		while (nodeUser.isTradeStartOrStop()) {
			try {

				nodeUser.setStateTrade(TradeState.BAY);
				strategyTrade.trade(nodeUser, change);
				nodeUser = nodeUserDAO.findByTelegramUserId(nodeUser.getId()).orElse(nodeUser);
			}catch (Exception e){
				nodeUser.setTradeStartOrStop(false);
				logger.error(e.getMessage());
			}
		}

	}


}
