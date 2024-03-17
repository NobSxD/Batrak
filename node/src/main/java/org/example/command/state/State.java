package org.example.command.state;


import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;

public interface State {
	String send(NodeUser nodeUser, String text);
	UserState getType();
	Account getAccount(Account account);

}
