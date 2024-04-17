package org.example.command.change;


import org.example.entity.enums.Menu1Enums;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;

import java.util.List;

public interface Change {

    void tradeStart();
    void tradeStop();
    List<String> pair();

	void saveAccount(Account account, NodeUser nodeUser);

	Account getAccount();

	List<Account> getAccounts();
	Menu1Enums getType();
	Account getAccount(String nameChange);
}
