package org.example.command.change.impl;

import lombok.Data;
import org.example.command.change.Change;
import org.example.dao.AccountBinanceDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.account.AccountBinance;
import org.example.entity.enums.Menu1Enums;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class ChangeBinanceImpl implements Change {
	private final AccountBinanceDAO accountBinanceDAO;
	@Override
	public void tradeStart() {

	}

	@Override
	public void tradeStop() {

	}

	@Override
	public List<String> pair() {
		return null;
	}

	@Override
	public void saveAccount(Account account, NodeUser nodeUser) {
		AccountBinance accountBinance = (AccountBinance) account;
		accountBinance.setNodeUser(nodeUser);
		accountBinanceDAO.save(accountBinance);
	}

	@Override
	public Account getAccount() {
		return new  AccountBinance();
	}

	@Override
	public List<Account> getAccounts() {
		List<AccountBinance> all = accountBinanceDAO.findAll();
		return new ArrayList<>(all);
	}

	@Override
	public Menu1Enums getType() {
		return Menu1Enums.Binance;
	}

	@Override
	public Account getAccount(String nameChange) {
		return accountBinanceDAO.findByNameChange(nameChange);
	}
}
