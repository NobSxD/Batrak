package org.example.service.impl;

import org.example.service.NodeAccount;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.example.entity.Account;
import org.example.entity.NodeUser;

import org.example.dao.AccountBaseDAO;

import org.springframework.stereotype.Service;
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
		account.setChangeType(nodeUser.getChangeType());
		return account;
	}

	@Override
	public List<Account> getAccounts(NodeUser nodeUser) {
		return accountBaseDAO.findAllByChangeTypeAndNodeUser(nodeUser.getChangeType(), nodeUser);
	}

	@Override
	public boolean existsByNodeUser(NodeUser nodeUser){
		return accountBaseDAO.existsByChangeTypeAndNodeUser(nodeUser.getChangeType(),nodeUser);
	}

	@Override
	public Account getAccount(String nameAccount, NodeUser nodeUser) {
		return accountBaseDAO.findByNameAccountAndChangeTypeAndNodeUser(nameAccount, nodeUser.getChangeType(), nodeUser).orElse(null);
	}

	@Override
	public void deleteFindId(long id) {
		accountBaseDAO.deleteById(id);
	}

}
