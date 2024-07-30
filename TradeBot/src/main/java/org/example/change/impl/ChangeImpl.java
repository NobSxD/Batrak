package org.example.change.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class ChangeImpl implements Change {
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
			log.error(e.getMessage() + " имя пользователя: " +  nodeUser.getUsername() + ". Id пользователя " + nodeUser.getId());
		} catch (Exception e) {
			nodeUser.setTradeStartOrStop(false);
			log.error(e.getMessage() + " имя пользователя: " +  nodeUser.getUsername() + " id пользователя " + nodeUser.getId());
		}
	}

	@Override
	public void infoAccount(NodeUser nodeUser) {
		try {
			BasicChangeInterface change = ChangeFactory.createChange(nodeUser);
			processServiceCommand.sendAnswer( change.accountInfo(), nodeUser.getChatId());
		}catch (ExchangeException e){
			processServiceCommand.sendAnswer("Проверте правелность введных данных, api public key, api secret key", nodeUser.getChatId());
			log.error(e.getMessage() + "имя пользователя: " +  nodeUser.getUsername() + ". Id пользователя " + nodeUser.getId());
		}
		catch (Exception e) {
			nodeUser.setTradeStartOrStop(false);
			log.error(e.getMessage() + "имя пользователя: " +  nodeUser.getUsername() + " id пользователя " + nodeUser.getId());
		}
	}

	@Override
	public void infoBalanceAccount(NodeUser nodeUser) {

	}

	@Override
	public void cancelAllOrder(NodeUser nodeUser) {

	}


}
