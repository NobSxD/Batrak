package org.example.change.impl;

import lombok.RequiredArgsConstructor;
import org.example.change.Change;
import org.example.change.ChangeFactory;
import org.example.dao.AccountBaseDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.TradeState;
import org.example.strategy.StrategyTrade;
import org.example.xchange.BasicChangeInterface;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ChangeImpl implements Change {
	private final StrategyTrade strategyTrade;
	private final AccountBaseDAO accountBaseDAO;
	private final NodeUserDAO nodeUserDAO;

	@Override
	public void tradeStart(NodeUser nodeUser) {
		nodeUser.setTradeStartOrStop(true);
		nodeUserDAO.save(nodeUser);
		TradeThread trade = new TradeThread(nodeUser, strategyTrade);
		Thread thread = new Thread(trade);
		thread.start();
	}

	@Override
	public void tradeStop(NodeUser nodeUser) {
		nodeUser.setTradeStartOrStop(false);
		nodeUserDAO.save(nodeUser);
	}

	@Override
	public void saveAccount(Account account, NodeUser nodeUser) {
		account.setNodeUser(nodeUser);
		accountBaseDAO.save(account);
	}

	@Override
	public Account newAccount(NodeUser nodeUser) {
		Account account = new Account();
		account.setNodeUser(nodeUser);
		account.setChangeType(nodeUser.getChangeType());
		return account;
	}

	@Override
	public List<Account> getAccounts(NodeUser nodeUser) {
		return accountBaseDAO.findAllByChangeTypeAndNodeUser(nodeUser.getChangeType(), nodeUser);
	}

	@Override
	public Account getAccount(String nameAccount, NodeUser nodeUser) {
		return accountBaseDAO.findByNameAccountAndChangeTypeAndNodeUser(nameAccount, nodeUser.getChangeType(), nodeUser);
	}

	@Override
	public void deleteFindId(long id) {
		accountBaseDAO.deleteById(id);
	}

	class TradeThread implements Runnable {
		NodeUser nodeUser;
		BasicChangeInterface change;
		StrategyTrade strategyTrade;

		public TradeThread(NodeUser nodeUser, StrategyTrade strategyTrade) {
			this.nodeUser = nodeUser;
			this.strategyTrade = strategyTrade;
			change = ChangeFactory.createChange(nodeUser);
		}

		@Override
		public void run() {
			while (nodeUser.isTradeStartOrStop()) {
				nodeUser.setStateTrade(TradeState.BAY);
				strategyTrade.trade(nodeUser, change);
			}
		}
	}
}
