package org.example.change.impl;

import lombok.Data;
import org.example.change.Change;
import org.example.dao.AccountMexcDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.account.AccountBinance;
import org.example.entity.account.AccountMexc;
import org.example.entity.enams.Menu1Enums;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class ChangeMexcImpl implements Change {
	private final AccountMexcDAO accountMexcDAO;
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
		AccountMexc accountMexc = (AccountMexc) account;
		accountMexc.setNodeUser(nodeUser);
		accountMexcDAO.save(accountMexc);
	}

	@Override
	public Account newAccount() {
		return new AccountBinance();
	}

	@Override
	public List<Account> getAccounts() {
		List<AccountMexc> all = accountMexcDAO.findAll();
		return new ArrayList<>(all);
	}

	@Override
	public Menu1Enums getType() {
		return Menu1Enums.Mex;
	}

	@Override
	public Account getAccount(String nameChange){
		return accountMexcDAO.findByNameChange(nameChange);
	}
}
