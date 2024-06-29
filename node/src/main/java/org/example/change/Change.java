package org.example.change;


import org.example.entity.NodeUser;
import org.example.entity.Account;

import java.util.List;

public interface Change {

	void saveAccount(Account account, NodeUser nodeUser);

	Account newAccount(NodeUser nodeUser);

	List<Account> getAccounts(NodeUser nodeUser);

	Account getAccount(String nameAccount, NodeUser nodeUser);

	void deleteFindId(long id);
}
