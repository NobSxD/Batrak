package org.example.change;


import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.Menu1Enums;

import java.util.List;

public interface Change {

    void tradeStart();
    void tradeStop();
    List<String> pair();

	void saveAccount(Account account, NodeUser nodeUser);

	Account newAccount();

	List<Account> getAccounts();
	Menu1Enums getType();
	Account getAccount(String nameChange);
}
