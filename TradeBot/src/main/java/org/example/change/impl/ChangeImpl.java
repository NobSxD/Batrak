package org.example.change.impl;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.change.Change;
import org.example.change.ChangeFactory;
import org.example.entity.NodeUser;
import org.example.entity.enams.TradeState;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.StrategyCommand;
import org.example.xchange.BasicChangeInterface;
import org.knowm.xchange.exceptions.ExchangeException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeImpl implements Change {
	private final Logger logger = Logger.getLogger(ChangeImpl.class);
	private final StrategyCommand strategyCommand;
	private final ProcessServiceCommand processServiceCommand;



	@Override
	public void instance(NodeUser nodeUser){
		nodeUser.setStateTrade(TradeState.BAY);
		tradeStart(nodeUser);
	}


	public void tradeStart(NodeUser nodeUser) {
		try {
			BasicChangeInterface change = ChangeFactory.createChange(nodeUser);
			strategyCommand.trade(nodeUser, change);

		} catch (ExchangeException e) {
			processServiceCommand.sendAnswer("Проверте правелность введных данных, api public key, api secret key", nodeUser.getChatId());
			logger.error(e.getMessage() + " имя пользователя: " +  nodeUser.getUsername() + ". Id пользователя " + nodeUser.getId());
		} catch (Exception e) {
			nodeUser.setTradeStartOrStop(false);
			logger.error(e.getMessage() + " имя пользователя: " +  nodeUser.getUsername() + " id пользователя " + nodeUser.getId());
		}
	}

	@Override
	public void infoAccount(NodeUser nodeUser) {
		try {
			BasicChangeInterface change = ChangeFactory.createChange(nodeUser);
			processServiceCommand.sendAnswer( change.accountInfo(), nodeUser.getChatId());
		}catch (ExchangeException e){
			processServiceCommand.sendAnswer("Проверте правелность введных данных, api public key, api secret key", nodeUser.getChatId());
			logger.error(e.getMessage() + "имя пользователя: " +  nodeUser.getUsername() + ". Id пользователя " + nodeUser.getId());
		}
		catch (Exception e) {
			nodeUser.setTradeStartOrStop(false);
			logger.error(e.getMessage() + "имя пользователя: " +  nodeUser.getUsername() + " id пользователя " + nodeUser.getId());
		}
	}

	@Override
	public void infoBalanceAccount(NodeUser nodeUser) {

	}

	@Override
	public void cancelAllOrder(NodeUser nodeUser) {

	}


}
