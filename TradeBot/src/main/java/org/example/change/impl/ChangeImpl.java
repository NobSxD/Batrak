package org.example.change.impl;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.change.Change;
import org.example.change.ChangeFactory;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.TradeState;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.StrategyTrade;
import org.example.xchange.BasicChangeInterface;
import org.knowm.xchange.exceptions.ExchangeException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeImpl implements Change {
	private final Logger logger = Logger.getLogger(ChangeImpl.class);
	private final StrategyTrade strategyTrade;
	private final NodeUserDAO nodeUserDAO;
	private final ProcessServiceCommand processServiceCommand;

	@Override
	public void tradeStart(NodeUser nodeUser) {
		try {
			BasicChangeInterface change = ChangeFactory.createChange(nodeUser);

			while (nodeUser.isTradeStartOrStop()) {
				nodeUser.setStateTrade(TradeState.BAY);
				strategyTrade.trade(nodeUser, change);
				nodeUser = nodeUserDAO.findByTelegramUserId(nodeUser.getId()).orElse(nodeUser);
			}
		} catch (ExchangeException e){
			processServiceCommand.sendAnswer("Проверте правелность введных данных, api public key, api secret key", nodeUser.getChatId());
			logger.error(e.getMessage() + "имя пользователя: " +  nodeUser.getUsername() + ". Id пользователя " + nodeUser.getId());
		}
		catch (Exception e) {
			nodeUser.setTradeStartOrStop(false);
			logger.error(e.getMessage() + "имя пользователя: " +  nodeUser.getUsername() + " id пользователя " + nodeUser.getId());
		}
	}


}
