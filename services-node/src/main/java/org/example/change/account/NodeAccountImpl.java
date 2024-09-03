package org.example.change.account;

import org.example.dao.AccountBaseDAO;
import org.example.entity.Account;
import org.example.entity.NodeUser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class NodeAccountImpl implements NodeAccount {

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
		account.setMenuChange(nodeUser.getMenuChange());
		return account;
	}

	@Override
	public List<Account> getAccounts(NodeUser nodeUser) {
		return accountBaseDAO.findAllByMenuChangeAndNodeUser(nodeUser.getMenuChange(), nodeUser);
	}

	@Override
	public Account getAccount(String nameAccount, NodeUser nodeUser) {
		return accountBaseDAO.findByNameAccountAndMenuChangeAndNodeUser(nameAccount, nodeUser.getMenuChange(), nodeUser).orElse(null);
	}

	@Override
	public void deleteFindId(long id) {
		accountBaseDAO.deleteById(id);
	}

}
