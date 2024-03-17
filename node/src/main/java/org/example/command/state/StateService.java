package org.example.command.state;


import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;

public interface StateService {
	String send(NodeUser nodeUser, String text);
	Account getAccount(Account account, UserState userState);
}
