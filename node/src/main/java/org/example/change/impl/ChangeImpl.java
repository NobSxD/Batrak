package org.example.change.impl;

import lombok.RequiredArgsConstructor;
import org.example.change.Change;
import org.example.dao.AccountBaseDAO;
import org.example.entity.Account;
import org.example.entity.NodeUser;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ChangeImpl implements Change {

	private final AccountBaseDAO accountBaseDAO;

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

}
